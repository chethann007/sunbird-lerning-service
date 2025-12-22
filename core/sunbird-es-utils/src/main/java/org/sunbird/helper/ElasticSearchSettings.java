package org.sunbird.helper;

/**
 * Provides Elasticsearch index settings with custom analyzers.
 *
 * <p>Defines analyzer configurations for indexing and searching including n-gram tokenization
 * and case normalization filters for text analysis.
 */
public class ElasticSearchSettings {

  /**
   * Gets the default Elasticsearch settings configuration.
   *
   * <p>Returns a JSON settings string that defines custom analyzers including cs_index_analyzer,
   * cs_search_analyzer, and keylower with n-gram tokenization and lowercase filters.
   *
   * @return the settings configuration as a JSON string
   */
  public static String createSettingsForIndex() {
    String settings =
        "{\"analysis\": {\"analyzer\": {\"cs_index_analyzer\": {\"type\": \"custom\",\"tokenizer\": \"standard\",\"filter\": [\"lowercase\",\"mynGram\"]},\"cs_search_analyzer\": {\"type\": \"custom\",\"tokenizer\": \"standard\",\"filter\": [\"lowercase\",\"standard\"]},\"keylower\": {\"type\": \"custom\",\"tokenizer\": \"keyword\",\"filter\": \"lowercase\"}},\"filter\": {\"mynGram\": {\"type\": \"ngram\",\"min_gram\": 1,\"max_gram\": 20,\"token_chars\": [\"letter\", \"digit\",\"whitespace\",\"punctuation\",\"symbol\"]} }}}";
    return settings;
  }
}

