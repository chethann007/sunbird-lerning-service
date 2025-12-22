package org.sunbird.helper;

import com.datastax.driver.core.Session;
import java.util.List;

/**
 * Manages Cassandra cluster connections and session management.
 * 
 * <p>Handles creation and retrieval of Cassandra sessions, cluster metadata lookup,
 * and table enumeration for configured keyspaces.
 */
public interface CassandraConnectionManager {

  /**
   * Creates a Cassandra cluster connection with the specified hosts.
   *
   * @param hosts Array of Cassandra host addresses/hostnames
   */
  void createConnection(String[] hosts);

  /**
   * Retrieves a Cassandra session for the specified keyspace.
   *
   * @param keyspaceName The keyspace name
   * @return A Cassandra session connected to the specified keyspace
   */
  Session getSession(String keyspaceName);

  /**
   * Retrieves the list of table names in the specified keyspace.
   *
   * @param keyspaceName The keyspace name
   * @return List of table names in the keyspace
   */
  List<String> getTableList(String keyspaceName);
}

