package org.sunbird.helper;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.common.CassandraPropertyReader;
import org.sunbird.common.Constants;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.util.ProjectUtil;

/**
 * Implementation of Cassandra connection manager.
 * 
 * <p>Manages Cassandra cluster connections, session pooling, and resource cleanup.
 * Uses connection pool configuration from properties and supports multi-datacenter setups.
 */
public class CassandraConnectionManagerImpl implements CassandraConnectionManager {
  private static final LoggerUtil logger = new LoggerUtil(CassandraConnectionManagerImpl.class);

  private static Cluster cluster;
  private static final Map<String, Session> cassandraSessionMap = new ConcurrentHashMap<>(2);

  static {
    registerShutDownHook();
  }

  @Override
  public void createConnection(String[] hosts) {
    createCassandraConnection(hosts);
  }

  @Override
  public Session getSession(String keyspace) {
    Session session = cassandraSessionMap.get(keyspace);
    if (null != session) {
      return session;
    } else {
      Session session2 = cluster.connect(keyspace);
      cassandraSessionMap.put(keyspace, session2);
      return session2;
    }
  }

  /**
   * Initializes Cassandra cluster connection with configured connection pool settings.
   * 
   * <p>Reads connection pool configuration from properties and establishes cluster connection
   * to specified hosts. Logs cluster metadata upon successful connection.
   *
   * @param hosts Array of Cassandra host addresses
   * @throws ProjectCommonException If connection fails
   */
  private void createCassandraConnection(String[] hosts) {
    try {
      CassandraPropertyReader cache = CassandraPropertyReader.getInstance();
      PoolingOptions poolingOptions = new PoolingOptions();
      poolingOptions.setCoreConnectionsPerHost(
          HostDistance.LOCAL,
          Integer.parseInt(cache.getProperty(Constants.CORE_CONNECTIONS_PER_HOST_FOR_LOCAL)));
      poolingOptions.setMaxConnectionsPerHost(
          HostDistance.LOCAL,
          Integer.parseInt(cache.getProperty(Constants.MAX_CONNECTIONS_PER_HOST_FOR_LOCAl)));
      poolingOptions.setCoreConnectionsPerHost(
          HostDistance.REMOTE,
          Integer.parseInt(cache.getProperty(Constants.CORE_CONNECTIONS_PER_HOST_FOR_REMOTE)));
      poolingOptions.setMaxConnectionsPerHost(
          HostDistance.REMOTE,
          Integer.parseInt(cache.getProperty(Constants.MAX_CONNECTIONS_PER_HOST_FOR_REMOTE)));
      poolingOptions.setMaxRequestsPerConnection(
          HostDistance.LOCAL,
          Integer.parseInt(cache.getProperty(Constants.MAX_REQUEST_PER_CONNECTION)));
      poolingOptions.setHeartbeatIntervalSeconds(
          Integer.parseInt(cache.getProperty(Constants.HEARTBEAT_INTERVAL)));
      poolingOptions.setPoolTimeoutMillis(
          Integer.parseInt(cache.getProperty(Constants.POOL_TIMEOUT)));

      //check for multi DC enabled or not from configuration file and send the value
      cluster = createCluster(hosts, poolingOptions, Boolean.parseBoolean(cache.getProperty(Constants.IS_MULTI_DC_ENABLED)));


      final Metadata metadata = cluster.getMetadata();
      String msg = String.format("Connected to cluster: %s", metadata.getClusterName());
      logger.info("createCassandraConnection :" + msg);

      for (final Host host : metadata.getAllHosts()) {
        msg =
            String.format(
                "Datacenter: %s; Host: %s; Rack: %s",
                host.getDatacenter(), host.getAddress(), host.getRack());
      }
    } catch (Exception e) {
      logger.error("Error occured while creating cassandra connection :", e);
      throw new ProjectCommonException(
          ResponseCode.serverError, e.getMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
    }
  }

  /**
   * Builds and configures a Cassandra cluster with pooling and load balancing policies.
   * 
   * <p>Applies connection pool configuration, consistency level, protocol version, and
   * load balancing policy (multi-DC aware if enabled).
   *
   * @param hosts Array of Cassandra host addresses
   * @param poolingOptions Connection pool configuration
   * @param isMultiDCEnabled Whether to enable multi-datacenter load balancing
   * @return Configured Cassandra cluster instance
   */
  private static Cluster createCluster(String[] hosts, PoolingOptions poolingOptions, boolean isMultiDCEnabled) {
    Cluster.Builder builder =
        Cluster.builder()
            .addContactPoints(hosts)
            .withProtocolVersion(ProtocolVersion.V3)
            .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
            .withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
            .withPoolingOptions(poolingOptions);

    ConsistencyLevel consistencyLevel = getConsistencyLevel();
    logger.info(
        "CassandraConnectionManagerImpl:createCluster: Consistency level = " + consistencyLevel);

    if (consistencyLevel != null) {
      builder.withQueryOptions(new QueryOptions().setConsistencyLevel(consistencyLevel));
    }

    logger.info(
            "CassandraConnectionManagerImpl:createCluster: isMultiDCEnabled = " + isMultiDCEnabled);
    if (isMultiDCEnabled) {
      builder.withLoadBalancingPolicy(DCAwareRoundRobinPolicy.builder().build());
    }

    return builder.build();
  }

  /**
   * Retrieves consistency level from configuration.
   * 
   * <p>Reads consistency level from configuration property and parses it into
   * a ConsistencyLevel enum value. Returns null if not configured or invalid.
   *
   * @return ConsistencyLevel if configured; null otherwise
   */
  private static ConsistencyLevel getConsistencyLevel() {
    String consistency = ProjectUtil.getConfigValue(JsonKey.SUNBIRD_CASSANDRA_CONSISTENCY_LEVEL);

    logger.info("CassandraConnectionManagerImpl:getConsistencyLevel: level = " + consistency);

    if (StringUtils.isBlank(consistency)) return null;

    try {
      return ConsistencyLevel.valueOf(consistency.toUpperCase());
    } catch (IllegalArgumentException exception) {
      logger.error(
          "CassandraConnectionManagerImpl:getConsistencyLevel: Exception occurred with error message = "
              + exception.getMessage(),
          exception);
    }
    return null;
  }

  @Override
  public List<String> getTableList(String keyspacename) {
    Collection<TableMetadata> tables = cluster.getMetadata().getKeyspace(keyspacename).getTables();

    // to convert to list of the names
    return tables.stream().map(tm -> tm.getName()).collect(Collectors.toList());
  }

  /**
   * Registers JVM shutdown hook for resource cleanup.
   * 
   * <p>Ensures Cassandra sessions and cluster connections are properly closed
   * when the JVM shuts down.
   */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
  }

  /**
   * Thread for cleaning up Cassandra resources on JVM shutdown.
   * 
   * <p>Closes all cached sessions and the cluster connection when invoked by
   * the JVM shutdown hook.
   */
  static class ResourceCleanUp extends Thread {
    @Override
    public void run() {
      try {
        for (Map.Entry<String, Session> entry : cassandraSessionMap.entrySet()) {
          cassandraSessionMap.get(entry.getKey()).close();
        }
        cluster.close();
      } catch (Exception ex) {
        logger.error("Error :", ex);
      }
    }
  }
}

