package org.sunbird.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.logging.LoggerUtil;

/**
 * Reads Cassandra configuration properties from resource files.
 * 
 * <p>Loads properties from {@code cassandratablecolumn.properties} and
 * {@code cassandra.config.properties} files using a lazy-initialized singleton pattern.
 * Supports environment variable overrides for property values.
 */
public class CassandraPropertyReader {
  private static final LoggerUtil logger = new LoggerUtil(CassandraPropertyReader.class);

  private final Properties properties = new Properties();
  private final String[] fileName = {
    "cassandratablecolumn.properties", "cassandra.config.properties"
  };
  private static CassandraPropertyReader cassandraPropertyReader = null;

  /** private default constructor */
  private CassandraPropertyReader() {
    for (String file : fileName) {
      InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
      try {
        properties.load(in);
      } catch (IOException e) {
        logger.error("Error in properties cache", e);
      }
    }
  }

  public static CassandraPropertyReader getInstance() {
    if (null == cassandraPropertyReader) {
      synchronized (CassandraPropertyReader.class) {
        if (null == cassandraPropertyReader) {
          cassandraPropertyReader = new CassandraPropertyReader();
        }
      }
    }
    return cassandraPropertyReader;
  }

  /**
   * Reads a property value from the loaded properties files.
   *
   * @param key The property key to read
   * @return The property value if found; otherwise returns the key itself
   */
  public String readProperty(String key) {
    return properties.getProperty(key) != null ? properties.getProperty(key) : key;
  }

  /**
   * Reads a property value with environment variable override support.
   *
   * @param key The property key to read
   * @return The environment variable value if set; otherwise returns the property value
   *         from loaded files; returns the key itself if not found
   */
  public String getProperty(String key) {
    String value = System.getenv(key);
    if (StringUtils.isNotBlank(value)) return value;
    return properties.getProperty(key) != null ? properties.getProperty(key) : key;
  }
}

