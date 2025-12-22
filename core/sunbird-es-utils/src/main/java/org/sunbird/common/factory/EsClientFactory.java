package org.sunbird.common.factory;

import org.sunbird.common.ElasticSearchRestHighImpl;
import org.sunbird.common.inf.ElasticSearchService;
import org.sunbird.keys.JsonKey;

/**
 * Factory class for creating Elasticsearch service client instances.
 *
 * <p>Provides singleton access to ElasticSearchService implementations using the factory pattern.
 * Supports multiple client types (REST, TCP) with lazy initialization and thread-safe instance
 * creation.
 */
public class EsClientFactory {

  private static ElasticSearchService restClient = null;

  /**
   * Gets an Elasticsearch service client instance for the specified type.
   *
   * @param type the client type ("rest" for REST high-level client)
   * @return ElasticSearchService implementation instance, or null if type is not supported
   */
  public static ElasticSearchService getInstance(String type) {
    if (JsonKey.REST.equals(type)) {
      return getRestClient();
    }
    return null;
  }

  /**
   * Gets the singleton REST high-level client instance.
   *
   * <p>Uses double-checked locking for thread-safe lazy initialization of the
   * ElasticSearchRestHighImpl client.
   *
   * @return the singleton ElasticSearchService REST client instance
   */
  private static ElasticSearchService getRestClient() {
    if (restClient == null) {
      synchronized (EsClientFactory.class) {
        if (restClient == null) {
          restClient = new ElasticSearchRestHighImpl();
        }
      }
    }
    return restClient;
  }
}
