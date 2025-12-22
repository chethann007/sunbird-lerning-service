package org.sunbird.helper;

/**
 * Provides Elasticsearch index mapping definitions.
 *
 * <p>Defines dynamic templates for automatic type detection and field analysis configuration
 * including custom analyzers for indexing and searching. Includes dynamic templates for longs,
 * booleans, doubles, dates, and strings with appropriate field mappings and analyzers.
 */
public class ElasticSearchMapping {

  /**
   * Gets the default Elasticsearch mapping configuration.
   *
   * <p>Returns a JSON mapping string that defines dynamic templates for various data types
   * (long, boolean, double, date, string) with custom analyzers for text fields. Includes
   * an "all_fields" property for aggregated full-text search.
   *
   * @return the mapping configuration as a JSON string
   */
  public static String createMapping() {
    String mapping =
        "  {  \"dynamic_templates\": [ {\"longs\": {\"match_mapping_type\": \"long\", \"mapping\": {\"type\": \"long\", \"fields\": { \"raw\": {\"type\": \"long\"  } }}}},{\"booleans\": {\"match_mapping_type\": \"boolean\", \"mapping\": {\"type\": \"boolean\", \"fields\": { \"raw\": { \"type\": \"boolean\" }} }}},{\"doubles\": {\"match_mapping_type\": \"double\",\"mapping\": {\"type\": \"double\",\"fields\":{\"raw\": { \"type\": \"double\" } }}}},{ \"dates\": {\"match_mapping_type\": \"date\", \"mapping\": { \"type\": \"date\",\"fields\": {\"raw\": { \"type\": \"date\" } } }}},{\"strings\": {\"match_mapping_type\": \"string\",\"mapping\": {\"type\": \"text\",\"fielddata\": true,\"copy_to\": \"all_fields\",\"analyzer\": \"cs_index_analyzer\",\"search_analyzer\": \"cs_search_analyzer\",\"fields\": {\"raw\": {\"type\": \"text\",\"fielddata\": true,\"analyzer\": \"keylower\"}}}}}],\"properties\": {\"all_fields\": {\"type\": \"text\",\"analyzer\": \"cs_index_analyzer\",\"search_analyzer\": \"cs_search_analyzer\",\"fields\": { \"raw\": { \"type\": \"text\",\"analyzer\": \"keylower\" } }} }}";
    return mapping;
  }
}

