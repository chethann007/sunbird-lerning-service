package org.sunbird.helper;

import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.cassandraimpl.CassandraDACImpl;

/**
 * Factory for creating Cassandra data access operation instances.
 */
public class ServiceFactory {
  private static CassandraOperation operation = null;

  private ServiceFactory() {}

  /**
   * Returns the singleton Cassandra operation instance.
   * 
   * <p>Uses double-checked locking pattern for thread-safe lazy initialization.
   *
   * @return The singleton CassandraOperation instance
   */
  public static CassandraOperation getInstance() {
    if (null == operation) {
      synchronized (ServiceFactory.class) {
        if (null == operation) {
          operation = new CassandraDACImpl();
        }
      }
    }
    return operation;
  }
}

