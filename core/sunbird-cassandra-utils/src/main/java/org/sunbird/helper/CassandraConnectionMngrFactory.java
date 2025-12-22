package org.sunbird.helper;

/**
 * Factory for creating singleton Cassandra connection manager instance.
 */
public class CassandraConnectionMngrFactory {

  private static CassandraConnectionManager instance;

  private CassandraConnectionMngrFactory() {}

  /**
   * Returns the singleton Cassandra connection manager instance.
   * 
   * <p>Uses double-checked locking pattern for thread-safe lazy initialization.
   *
   * @return The singleton CassandraConnectionManager instance
   */
  public static CassandraConnectionManager getInstance() {
    if (instance == null) {
      synchronized (CassandraConnectionMngrFactory.class) {
        if (instance == null) {
          instance = new CassandraConnectionManagerImpl();
        }
      }
    }
    return instance;
  }
}

