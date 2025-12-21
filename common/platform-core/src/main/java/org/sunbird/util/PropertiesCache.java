package org.sunbird.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.logging.LoggerUtil;

/**
 * Singleton cache for loading and managing application configuration properties.
 * 
 * <p>This class loads properties from multiple configuration files and provides
 * thread-safe access to configuration values. It implements the singleton pattern
 * to ensure a single instance manages all application properties.
 * 
 * <p>Configuration files loaded (in order):
 * <ul>
 *   <li>elasticsearch.config.properties</li>
 *   <li>dbconfig.properties</li>
 *   <li>externalresource.properties</li>
 *   <li>sso.properties</li>
 *   <li>userencryption.properties</li>
 *   <li>mailTemplates.properties</li>
 * </ul>
 * 
 * <p>Property resolution priority:
 * <ol>
 *   <li>System environment variables (via getProperty)</li>
 *   <li>Loaded configuration files (via getProperty)</li>
 *   <li>Key itself as fallback (via getProperty)</li>
 * </ol>
 */
public class PropertiesCache {

  /** Logger instance for this class */
  private static LoggerUtil logger = new LoggerUtil(PropertiesCache.class);

  /** Configuration file names to load */
  private final String[] fileName = {
    "elasticsearch.config.properties",
    "dbconfig.properties",
    "externalresource.properties",
    "sso.properties",
    "userencryption.properties",
    "mailTemplates.properties"
  };

  /** Properties container */
  private final Properties configProp = new Properties();

  /** Singleton instance */
  private static PropertiesCache instance;

  /**
   * Private constructor that loads all configuration files.
   * Loads properties from all specified configuration files into memory.
   * Errors during loading are logged but do not prevent initialization.
   */
  private PropertiesCache() {
    for (String file : fileName) {
      InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
      try {
        configProp.load(in);
      } catch (IOException e) {
        logger.error("Error in properties cache", e);
      }
    }
  }

  /**
   * Gets the singleton instance of PropertiesCache.
   * Uses double-checked locking pattern for thread-safe lazy initialization.
   *
   * @return the singleton instance
   */
  public static PropertiesCache getInstance() {
    if (instance == null) {
      // To make thread safe
      synchronized (PropertiesCache.class) {
        // check again as multiple threads
        // can reach above step
        if (instance == null) {
          instance = new PropertiesCache();
        }
      }
    }
    return instance;
  }

  /**
   * Saves a configuration property to the in-memory cache.
   * This does not persist to disk.
   *
   * @param key the property key
   * @param value the property value
   */
  public void saveConfigProperty(String key, String value) {
    configProp.setProperty(key, value);
  }

  /**
   * Gets a configuration property with multiple fallback options.
   * Resolution order:
   * <ol>
   *   <li>System environment variable with the key name</li>
   *   <li>Configuration file property</li>
   *   <li>The key itself (if property not found)</li>
   * </ol>
   *
   * @param key the property key
   * @return the property value or key if not found
   */
  public String getProperty(String key) {
    String value = System.getenv(key);
    if (StringUtils.isNotBlank(value)) {
      return value;
    }
    return configProp.getProperty(key) != null ? configProp.getProperty(key) : key;
  }

  /**
   * Reads a property value from the configuration files.
   * Resolution order:
   * <ol>
   *   <li>System environment variable with the key name</li>
   *   <li>Configuration file property</li>
   * </ol>
   *
   * @param key the property key to read
   * @return the property value, or null if not found
   */
  public String readProperty(String key) {
    String value = System.getenv(key);
    if (StringUtils.isNotBlank(value)) {
      return value;
    }
    return configProp.getProperty(key);
  }
}

