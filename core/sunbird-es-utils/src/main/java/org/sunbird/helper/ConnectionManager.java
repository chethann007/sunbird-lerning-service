package org.sunbird.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;

/**
 * Manages Elasticsearch REST client connections.
 *
 * <p>Provides singleton access to a RestHighLevelClient configured via environment variables.
 * Handles initialization, connection pooling, and graceful shutdown of Elasticsearch connections.
 */
public class ConnectionManager {
  private static final LoggerUtil logger = new LoggerUtil(ConnectionManager.class);

  private static RestHighLevelClient restClient = null;
  private static List<String> host = new ArrayList<>();
  private static List<Integer> ports = new ArrayList<>();

  static {
    System.setProperty("es.set.netty.runtime.available.processors", "false");
    initialiseRestClientConnection();
    registerShutDownHook();
  }

  /** Private constructor to prevent instantiation. */
  private ConnectionManager() {}

  /**
   * Initializes the REST client connection by reading environment variables and creating the
   * Elasticsearch REST high-level client. Configures connection to cluster using SUNBIRD_ES_IP,
   * SUNBIRD_ES_PORT, and SUNBIRD_ES_CLUSTER environment variables.
   *
   * @return true if connection initialization is successful, false otherwise
   */
  private static boolean initialiseRestClientConnection() {
    boolean response = false;
    try {
      String cluster = System.getenv(JsonKey.SUNBIRD_ES_CLUSTER);
      String hostName = System.getenv(JsonKey.SUNBIRD_ES_IP);
      String port = System.getenv(JsonKey.SUNBIRD_ES_PORT);
      if (StringUtils.isBlank(hostName) || StringUtils.isBlank(port)) {
        return false;
      }
      String[] splitedHost = hostName.split(",");
      for (String val : splitedHost) {
        host.add(val);
      }
      String[] splitedPort = port.split(",");
      for (String val : splitedPort) {
        ports.add(Integer.parseInt(val));
      }
      response = createRestClient(cluster, host);
      logger.info(
          "ELASTIC SEARCH CONNECTION ESTABLISHED for restClient from EVN with Following Details cluster "
              + cluster
              + "  hostName"
              + hostName
              + " port "
              + port
              + response);
    } catch (Exception e) {
      logger.error("Error while initialising connection for restClient from the Env", e);
      return false;
    }
    return response;
  }

  /**
   * Gets the singleton Elasticsearch REST high-level client instance.
   *
   * <p>If the client is not initialized, attempts to initialize the connection. Returns null if
   * initialization fails.
   *
   * @return RestHighLevelClient instance or null if unavailable
   */
  public static RestHighLevelClient getRestClient() {
    if (restClient == null) {
      logger.info("ConnectionManager:getRestClient eLastic search rest clinet is null ");
      initialiseRestClientConnection();
      logger.info(
          "ConnectionManager:getRestClient after calling initialiseRestClientConnection ES client value ");
    }
    return restClient;
  }

  /**
   * Creates and initializes the Elasticsearch REST high-level client with the specified cluster
   * and hosts.
   *
   * @param clusterName the Elasticsearch cluster name
   * @param host list of host addresses for the cluster
   * @return true if client creation is successful
   */
  private static boolean createRestClient(String clusterName, List<String> host) {
    HttpHost[] httpHost = new HttpHost[host.size()];
    for (int i = 0; i < host.size(); i++) {
      httpHost[i] = new HttpHost(host.get(i), 9200);
    }
    restClient = new RestHighLevelClient(RestClient.builder(httpHost));
    logger.info("ConnectionManager:createRestClient client initialisation done. ");
    return true;
  }

  /**
   * Inner class that handles graceful shutdown of Elasticsearch REST client when the JVM
   * terminates.
   *
   * <p>Registered as a shutdown hook to ensure proper resource cleanup and connection closure.
   */
  public static class ResourceCleanUp extends Thread {
    @Override
    public void run() {
      try {
        if (null != restClient) {
          restClient.close();
        }
      } catch (IOException e) {
        logger.info(
            "ConnectionManager:ResourceCleanUp error occured during restclient resource cleanup "
                + e);
      }
    }
  }

  /**
   * Registers a shutdown hook with the JVM runtime to ensure proper cleanup of Elasticsearch
   * resources when the application terminates.
   */
  public static void registerShutDownHook() {
    Runtime runtime = Runtime.getRuntime();
    runtime.addShutdownHook(new ResourceCleanUp());
  }
}

