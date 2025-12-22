package org.sunbird.common;

import org.apache.pekko.util.Timeout;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.sort.SortOrder;
import org.sunbird.dto.SearchDTO;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;
import scala.concurrent.Await;
import scala.concurrent.Future;

/**
 * Helper class for Elasticsearch operations.
 *
 * <p>Provides utility methods for building search queries, handling aggregations, applying
 * filters, and processing search responses. Supports range queries, term queries, exists filters,
 * nested queries, and faceted search.
 */
public class ElasticSearchHelper {
  private static final LoggerUtil logger = new LoggerUtil(ElasticSearchHelper.class);
  public static final String LTE = "<=";
  public static final String LT = "<";
  public static final String GTE = ">=";
  public static final String GT = ">";
  public static final String ASC_ORDER = "ASC";
  public static final String STARTS_WITH = "startsWith";
  public static final String ENDS_WITH = "endsWith";
  public static final String RAW_APPEND = ".raw";
  public static final int WAIT_TIME = 5;
  public static Timeout timeout = new Timeout(WAIT_TIME, TimeUnit.SECONDS);
  public static final List<String> upsertResults =
      new ArrayList<>(Arrays.asList("CREATED", "UPDATED", "NOOP"));
  private static final String _DOC = "_doc";

  private ElasticSearchHelper() {}

  /**
   * Gets the result from a Scala Future by waiting for completion.
   *
   * @param future the Future to await
   * @return the result object from the Future, or null if an error occurs
   */
  @SuppressWarnings("unchecked")
  public static Object getResponseFromFuture(Future future) {
    try {
      Object result = Await.result(future, timeout.duration());
      return result;
    } catch (Exception e) {
      logger.error("getResponseFromFuture: error occured ", e);
    }
    return null;
  }

  /**
   * Adds aggregations to the search request builder.
   *
   * <p>Processes facets to create aggregation queries including date histograms and term
   * aggregations for grouped data analysis.
   *
   * @param searchRequestBuilder the builder to update with aggregations
   * @param facets list of facet definitions with field names and aggregation types
   * @return the updated SearchRequestBuilder with aggregations
   */
  public static SearchRequestBuilder addAggregations(
      SearchRequestBuilder searchRequestBuilder, List<Map<String, String>> facets) {
    long startTime = System.currentTimeMillis();
    logger.debug("addAggregations: method started at ==" + startTime);
    if (facets != null && !facets.isEmpty()) {
      Map<String, String> map = facets.get(0);
      if (!MapUtils.isEmpty(map)) {
        for (Map.Entry<String, String> entry : map.entrySet()) {

          String key = entry.getKey();
          String value = entry.getValue();
          if (JsonKey.DATE_HISTOGRAM.equalsIgnoreCase(value)) {
            searchRequestBuilder.addAggregation(
                AggregationBuilders.dateHistogram(key)
                    .field(key + RAW_APPEND)
                    .dateHistogramInterval(DateHistogramInterval.days(1)));

          } else if (null == value) {
            searchRequestBuilder.addAggregation(
                AggregationBuilders.terms(key).field(key + RAW_APPEND));
          }
        }
      }
      long elapsedTime = calculateEndTime(startTime);
      logger.debug(
          "ElasticSearchHelper:addAggregations method end =="
              + " ,Total time elapsed = "
              + elapsedTime);
    }

    return searchRequestBuilder;
  }

  /**
   * Extracts soft constraints from the search DTO.
   *
   * @param searchDTO the search DTO containing soft constraints
   * @return map of field names to boost values, or empty map if no constraints
   */
  public static Map<String, Float> getConstraints(SearchDTO searchDTO) {
    if (null != searchDTO.getSoftConstraints() && !searchDTO.getSoftConstraints().isEmpty()) {
      return searchDTO
          .getSoftConstraints()
          .entrySet()
          .stream()
          .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().floatValue()));
    }
    return Collections.emptyMap();
  }

  /**
   * Creates a search request builder for the transport client.
   *
   * @param client the TransportClient instance
   * @param index array of index names to search
   * @return SearchRequestBuilder configured for the specified indices
   */
  public static SearchRequestBuilder getTransportSearchBuilder(
      TransportClient client, String[] index) {
    return client.prepareSearch().setIndices(index).setTypes(_DOC);
  }

  /**
   * Adds additional query properties like range queries, exists/not-exists filters, and nested
   * queries to the boolean query.
   *
   * @param query the BoolQueryBuilder to update
   * @param entry the entry containing filter key and values
   * @param constraintsMap boost constraints for fields
   */
  @SuppressWarnings("unchecked")
  public static void addAdditionalProperties(
      BoolQueryBuilder query, Entry<String, Object> entry, Map<String, Float> constraintsMap) {
    long startTime = System.currentTimeMillis();
    logger.debug("ElasticSearchHelper:addAdditionalProperties: method started at ==" + startTime);
    String key = entry.getKey();
    if (JsonKey.FILTERS.equalsIgnoreCase(key)) {

      Map<String, Object> filters = (Map<String, Object>) entry.getValue();
      for (Map.Entry<String, Object> en : filters.entrySet()) {
        query = createFilterESOpperation(en, query, constraintsMap);
      }
    } else if (JsonKey.EXISTS.equalsIgnoreCase(key) || JsonKey.NOT_EXISTS.equalsIgnoreCase(key)) {
      query = createESOpperation(entry, query, constraintsMap);
    } else if (JsonKey.NESTED_EXISTS.equalsIgnoreCase(key)
        || JsonKey.NESTED_NOT_EXISTS.equalsIgnoreCase(key)) {
      query = createNestedESOpperation(entry, query, constraintsMap);
    } else if (JsonKey.NESTED_KEY_FILTER.equalsIgnoreCase(key)) {
      Map<String, Object> nestedFilters = (Map<String, Object>) entry.getValue();
      for (Map.Entry<String, Object> en : nestedFilters.entrySet()) {
        query = createNestedFilterESOpperation(en, query, constraintsMap);
      }
    }
    long elapsedTime = calculateEndTime(startTime);
    logger.debug(
        "ElasticSearchHelper:addAdditionalProperties: method end =="
            + " ,Total time elapsed = "
            + elapsedTime);
  }

  /**
   * Creates filter operations for Elasticsearch queries.
   *
   * <p>Handles term queries, range queries, and OR operations based on the filter entry value
   * type.
   *
   * @param entry the filter entry with key and value
   * @param query the BoolQueryBuilder to update
   * @param constraintsMap boost constraints for fields
   * @return the updated BoolQueryBuilder
   */
  @SuppressWarnings("unchecked")
  private static BoolQueryBuilder createFilterESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {
    String key = entry.getKey();
    Object val = entry.getValue();
    if (val instanceof List && val != null) {
      query = getTermQueryFromList(val, key, query, constraintsMap);
    } else if (val instanceof Map) {
      if (key.equalsIgnoreCase(JsonKey.ES_OR_OPERATION)) {
        query.must(createEsORFilterQuery((Map<String, Object>) val));
      } else {
        query = getTermQueryFromMap(val, key, query, constraintsMap);
      }
    } else if (val instanceof String) {
      query.must(
          createTermQuery(key + RAW_APPEND, ((String) val).toLowerCase(), constraintsMap.get(key)));
    } else {
      query.must(createTermQuery(key + RAW_APPEND, val, constraintsMap.get(key)));
    }
    return query;
  }

  /**
   * Creates nested filter operations for Elasticsearch queries.
   *
   * <p>Handles nested term queries, range queries, and nested path queries for complex document
   * structures.
   *
   * @param entry the filter entry with key and value
   * @param query the BoolQueryBuilder to update
   * @param constraintsMap boost constraints for fields
   * @return the updated BoolQueryBuilder
   */
  @SuppressWarnings("unchecked")
  private static BoolQueryBuilder createNestedFilterESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {
    String key = entry.getKey();
    Object val = entry.getValue();
    String path = key.split("\\.")[0];
    if (val instanceof List && CollectionUtils.isNotEmpty((List) val)) {
      if (((List) val).get(0) instanceof String) {
        ((List<String>) val).replaceAll(String::toLowerCase);
        query.must(
            QueryBuilders.nestedQuery(
                path,
                createTermsQuery(key + RAW_APPEND, (List<String>) val, constraintsMap.get(key)),
                ScoreMode.None));
      } else {
        query.must(
            QueryBuilders.nestedQuery(
                path, createTermsQuery(key, (List) val, constraintsMap.get(key)), ScoreMode.None));
      }
    } else if (val instanceof Map) {
      query = getNestedTermQueryFromMap(val, key, path, query, constraintsMap);
    } else if (val instanceof String) {
      query.must(
          QueryBuilders.nestedQuery(
              path,
              createTermQuery(
                  key + RAW_APPEND, ((String) val).toLowerCase(), constraintsMap.get(key)),
              ScoreMode.None));
    } else {
      query.must(
          QueryBuilders.nestedQuery(
              path,
              createTermQuery(key + RAW_APPEND, val, constraintsMap.get(key)),
              ScoreMode.None));
    }
    return query;
  }

  /**
   * Extracts and creates term queries from a Map filter value.
   *
   * <p>Handles range operations (LT, GT, LTE, GTE) and lexical operations (startsWith,
   * endsWith).
   *
   * @param val the filter value as a Map
   * @param key the field name
   * @param query the BoolQueryBuilder to update
   * @param constraintsMap boost constraints for fields
   * @return the updated BoolQueryBuilder
   */
  private static BoolQueryBuilder getTermQueryFromMap(
      Object val, String key, BoolQueryBuilder query, Map<String, Float> constraintsMap) {
    Map<String, Object> value = (Map<String, Object>) val;
    Map<String, Object> rangeOperation = new HashMap<>();
    Map<String, Object> lexicalOperation = new HashMap<>();
    for (Map.Entry<String, Object> it : value.entrySet()) {
      String operation = it.getKey();
      if (operation.startsWith(LT) || operation.startsWith(GT)) {
        rangeOperation.put(operation, it.getValue());
      } else if (operation.startsWith(STARTS_WITH) || operation.startsWith(ENDS_WITH)) {
        lexicalOperation.put(operation, it.getValue());
      }
    }
    if (!(rangeOperation.isEmpty())) {
      query.must(createRangeQuery(key, rangeOperation, constraintsMap.get(key)));
    }
    if (!(lexicalOperation.isEmpty())) {
      query.must(createLexicalQuery(key, lexicalOperation, constraintsMap.get(key)));
    }

    return query;
  }

  /**
   * Creates an OR filter query from multiple field-value pairs.
   *
   * @param orFilters map of field names to values for OR operation
   * @return BoolQueryBuilder with should clauses for OR logic
   */
  private static BoolQueryBuilder createEsORFilterQuery(Map<String, Object> orFilters) {
    BoolQueryBuilder query = new BoolQueryBuilder();
    for (Map.Entry<String, Object> mp : orFilters.entrySet()) {
      query.should(
          QueryBuilders.termQuery(
              mp.getKey() + RAW_APPEND, ((String) mp.getValue()).toLowerCase()));
    }
    return query;
  }

  /**
   * Extracts and creates nested term queries from a Map filter value.
   *
   * <p>Handles nested range and lexical operations within a nested document path.
   *
   * @param val the filter value as a Map
   * @param key the field name
   * @param path the nested document path
   * @param query the BoolQueryBuilder to update
   * @param constraintsMap boost constraints for fields
   * @return the updated BoolQueryBuilder
   */
  private static BoolQueryBuilder getNestedTermQueryFromMap(
      Object val,
      String key,
      String path,
      BoolQueryBuilder query,
      Map<String, Float> constraintsMap) {
    Map<String, Object> value = (Map<String, Object>) val;
    Map<String, Object> rangeOperation = new HashMap<>();
    Map<String, Object> lexicalOperation = new HashMap<>();
    for (Map.Entry<String, Object> it : value.entrySet()) {
      String operation = it.getKey();
      if (operation.startsWith(LT) || operation.startsWith(GT)) {
        rangeOperation.put(operation, it.getValue());
      } else if (operation.startsWith(STARTS_WITH) || operation.startsWith(ENDS_WITH)) {
        lexicalOperation.put(operation, it.getValue());
      }
    }
    if (!(rangeOperation.isEmpty())) {
      query.must(
          QueryBuilders.nestedQuery(
              path,
              createRangeQuery(key, rangeOperation, constraintsMap.get(key)),
              ScoreMode.None));
    }
    if (!(lexicalOperation.isEmpty())) {
      query.must(
          QueryBuilders.nestedQuery(
              path,
              createLexicalQuery(key, lexicalOperation, constraintsMap.get(key)),
              ScoreMode.None));
    }
    return query;
  }

  /**
   * Extracts and creates term queries from a List filter value.
   *
   * @param val the filter value as a List
   * @param key the field name
   * @param query the BoolQueryBuilder to update
   * @param constraintsMap boost constraints for fields
   * @return the updated BoolQueryBuilder
   */
  private static BoolQueryBuilder getTermQueryFromList(
      Object val, String key, BoolQueryBuilder query, Map<String, Float> constraintsMap) {
    if (!((List) val).isEmpty()) {
      if (((List) val).get(0) instanceof String) {
        ((List<String>) val).replaceAll(String::toLowerCase);
        query.must(createTermsQuery(key + RAW_APPEND, (List<String>) val, constraintsMap.get(key)));
      } else {
        query.must(createTermsQuery(key, (List) val, constraintsMap.get(key)));
      }
    }
    return query;
  }

  /**
   * Creates EXISTS and NOT EXISTS filter queries.
   *
   * @param entry the entry containing operation type and field names
   * @param query the BoolQueryBuilder to update
   * @param constraintsMap boost constraints for fields
   * @return the updated BoolQueryBuilder
   */
  @SuppressWarnings("unchecked")
  private static BoolQueryBuilder createESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {

    String operation = entry.getKey();
    if (entry.getValue() != null && entry.getValue() instanceof List) {
      List<String> existsList = (List<String>) entry.getValue();

      if (JsonKey.EXISTS.equalsIgnoreCase(operation)) {
        for (String name : existsList) {
          query.must(createExistQuery(name, constraintsMap.get(name)));
        }
      } else if (JsonKey.NOT_EXISTS.equalsIgnoreCase(operation)) {
        for (String name : existsList) {
          query.mustNot(createExistQuery(name, constraintsMap.get(name)));
        }
      }
    }
    return query;
  }

  /**
   * Creates nested EXISTS and NOT EXISTS filter queries.
   *
   * @param entry the entry containing operation type and field-path mappings
   * @param query the BoolQueryBuilder to update
   * @param constraintsMap boost constraints for fields
   * @return the updated BoolQueryBuilder
   */
  @SuppressWarnings("unchecked")
  private static BoolQueryBuilder createNestedESOpperation(
      Entry<String, Object> entry, BoolQueryBuilder query, Map<String, Float> constraintsMap) {

    String operation = entry.getKey();
    if (entry.getValue() != null && entry.getValue() instanceof Map) {
      Map<String, String> existsMap = (Map<String, String>) entry.getValue();

      if (JsonKey.NESTED_EXISTS.equalsIgnoreCase(operation)) {
        for (Map.Entry<String, String> nameByPath : existsMap.entrySet()) {
          query.must(
              QueryBuilders.nestedQuery(
                  nameByPath.getValue(),
                  createExistQuery(nameByPath.getKey(), constraintsMap.get(nameByPath.getKey())),
                  ScoreMode.None));
        }
      } else if (JsonKey.NESTED_NOT_EXISTS.equalsIgnoreCase(operation)) {
        for (Map.Entry<String, String> nameByPath : existsMap.entrySet()) {
          query.mustNot(
              QueryBuilders.nestedQuery(
                  nameByPath.getValue(),
                  createExistQuery(nameByPath.getKey(), constraintsMap.get(nameByPath.getKey())),
                  ScoreMode.None));
        }
      }
    }
    return query;
  }

  /**
   * Determines the sort order from a string value.
   *
   * @param value the sort order string ("ASC" for ascending, anything else for descending)
   * @return SortOrder enum value
   */
  public static SortOrder getSortOrder(String value) {
    return ASC_ORDER.equalsIgnoreCase(value) ? SortOrder.ASC : SortOrder.DESC;
  }

  /**
   * Creates a fuzzy match query for approximate string matching.
   *
   * @param query the BoolQueryBuilder to update
   * @param name the field name
   * @param value the value to match
   */
  public static void createFuzzyMatchQuery(BoolQueryBuilder query, String name, Object value) {
    query.must(
        QueryBuilders.matchQuery(name, value).fuzziness(Fuzziness.AUTO).fuzzyTranspositions(true));
  }

  /**
   * Creates a terms query for matching multiple values.
   *
   * @param key the field name
   * @param values list of values to match
   * @param boost optional boost value for query priority
   * @return TermsQueryBuilder
   */
  private static TermsQueryBuilder createTermsQuery(String key, List values, Float boost) {
    if (null != (boost)) {
      return QueryBuilders.termsQuery(key, (values).stream().toArray(Object[]::new)).boost(boost);
    } else {
      return QueryBuilders.termsQuery(key, (values).stream().toArray(Object[]::new));
    }
  }

  /**
   * Creates a range query for numeric or date range filtering.
   *
   * @param name the field name
   * @param rangeOperation map of range operators (LT, LTE, GT, GTE) to values
   * @param boost optional boost value for query priority
   * @return RangeQueryBuilder
   */
  private static RangeQueryBuilder createRangeQuery(
      String name, Map<String, Object> rangeOperation, Float boost) {

    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name + RAW_APPEND);
    for (Map.Entry<String, Object> it : rangeOperation.entrySet()) {
      switch (it.getKey()) {
        case LTE:
          rangeQueryBuilder.lte(it.getValue());
          break;
        case LT:
          rangeQueryBuilder.lt(it.getValue());
          break;
        case GTE:
          rangeQueryBuilder.gte(it.getValue());
          break;
        case GT:
          rangeQueryBuilder.gt(it.getValue());
          break;
      }
    }
    if (null != (boost)) {
      return rangeQueryBuilder.boost(boost);
    }
    return rangeQueryBuilder;
  }

  /**
   * Creates a term query for exact value matching.
   *
   * @param name the field name
   * @param value the value to match
   * @param boost optional boost value for query priority
   * @return TermQueryBuilder
   */
  private static TermQueryBuilder createTermQuery(String name, Object value, Float boost) {
    if (null != (boost)) {
      return QueryBuilders.termQuery(name, value).boost(boost);
    } else {
      return QueryBuilders.termQuery(name, value);
    }
  }

  /**
   * Creates an exists query to filter documents where a field exists.
   *
   * @param name the field name
   * @param boost optional boost value for query priority
   * @return ExistsQueryBuilder
   */
  private static ExistsQueryBuilder createExistQuery(String name, Float boost) {
    if (null != (boost)) {
      return QueryBuilders.existsQuery(name).boost(boost);
    } else {
      return QueryBuilders.existsQuery(name);
    }
  }

  /**
   * Creates a lexical query for prefix or suffix matching.
   *
   * @param key the field name
   * @param rangeOperation map with "startsWith" or "endsWith" operations
   * @param boost optional boost value for query priority
   * @return QueryBuilder (PrefixQuery or RegexpQuery)
   */
  public static QueryBuilder createLexicalQuery(
      String key, Map<String, Object> rangeOperation, Float boost) {
    QueryBuilder queryBuilder = null;
    for (Map.Entry<String, Object> it : rangeOperation.entrySet()) {
      switch (it.getKey()) {
        case STARTS_WITH:
          {
            String startsWithVal = (String) it.getValue();
            if (StringUtils.isNotBlank(startsWithVal)) {
              startsWithVal = startsWithVal.toLowerCase();
            }
            if (null != (boost)) {
              queryBuilder =
                  QueryBuilders.prefixQuery(key + RAW_APPEND, startsWithVal).boost(boost);
            }
            queryBuilder = QueryBuilders.prefixQuery(key + RAW_APPEND, startsWithVal);
            break;
          }
        case ENDS_WITH:
          {
            String endsWithRegex = "~" + it.getValue();
            if (null != (boost)) {
              queryBuilder =
                  QueryBuilders.regexpQuery(key + RAW_APPEND, endsWithRegex).boost(boost);
            }
            queryBuilder = QueryBuilders.regexpQuery(key + RAW_APPEND, endsWithRegex);
            break;
          }
      }
    }
    return queryBuilder;
  }

  /**
   * Calculates elapsed time from a start time.
   *
   * @param startTime the start time in milliseconds
   * @return elapsed time in milliseconds
   */
  public static long calculateEndTime(long startTime) {
    return System.currentTimeMillis() - startTime;
  }

  /**
   * Creates a SearchDTO from a search query map.
   *
   * @param searchQueryMap map containing search parameters
   * @return SearchDTO populated with query parameters
   */
  public static SearchDTO createSearchDTO(Map<String, Object> searchQueryMap) {
    SearchDTO search = new SearchDTO();
    search = getBasicBuiders(search, searchQueryMap);
    search = setOffset(search, searchQueryMap);
    search = getLimits(search, searchQueryMap);
    if (searchQueryMap.containsKey(JsonKey.GROUP_QUERY)) {
      search
          .getGroupQuery()
          .addAll(
              (Collection<? extends Map<String, Object>>) searchQueryMap.get(JsonKey.GROUP_QUERY));
    }
    search = getSoftConstraints(search, searchQueryMap);
    Map<String, String> fuzzy = (Map<String, String>) searchQueryMap.get(JsonKey.SEARCH_FUZZY);
    if (MapUtils.isNotEmpty(fuzzy)) {
      search.setFuzzy(fuzzy);
    }
    return search;
  }

  /**
   * Adds soft constraints to the SearchDTO.
   *
   * @param search the SearchDTO to update
   * @param searchQueryMap map containing soft constraints
   * @return updated SearchDTO
   */
  private static SearchDTO getSoftConstraints(
      SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.SOFT_CONSTRAINTS)) {
      search.setSoftConstraints(
          (Map<String, Integer>) searchQueryMap.get(JsonKey.SOFT_CONSTRAINTS));
    }
    return search;
  }

  /**
   * Adds limit parameter to the SearchDTO.
   *
   * @param search the SearchDTO to update
   * @param searchQueryMap map containing limit
   * @return updated SearchDTO
   */
  private static SearchDTO getLimits(SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.LIMIT)) {
      if ((searchQueryMap.get(JsonKey.LIMIT)) instanceof Integer) {
        search.setLimit((int) searchQueryMap.get(JsonKey.LIMIT));
      } else {
        search.setLimit(((BigInteger) searchQueryMap.get(JsonKey.LIMIT)).intValue());
      }
    }
    return search;
  }

  /**
   * Adds offset parameter to the SearchDTO.
   *
   * @param search the SearchDTO to update
   * @param searchQueryMap map containing offset
   * @return updated SearchDTO
   */
  private static SearchDTO setOffset(SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.OFFSET)) {
      if ((searchQueryMap.get(JsonKey.OFFSET)) instanceof Integer) {
        search.setOffset((int) searchQueryMap.get(JsonKey.OFFSET));
      } else {
        search.setOffset(((BigInteger) searchQueryMap.get(JsonKey.OFFSET)).intValue());
      }
    }
    return search;
  }

  /**
   * Adds basic query parameters to the SearchDTO.
   *
   * @param search the SearchDTO to update
   * @param searchQueryMap map containing query parameters
   * @return updated SearchDTO
   */
  private static SearchDTO getBasicBuiders(SearchDTO search, Map<String, Object> searchQueryMap) {
    if (searchQueryMap.containsKey(JsonKey.QUERY)) {
      search.setQuery((String) searchQueryMap.get(JsonKey.QUERY));
    }
    if (searchQueryMap.containsKey(JsonKey.QUERY_FIELDS)) {
      search.setQueryFields((List<String>) searchQueryMap.get(JsonKey.QUERY_FIELDS));
    }
    if (searchQueryMap.containsKey(JsonKey.FACETS)) {
      search.setFacets((List<Map<String, String>>) searchQueryMap.get(JsonKey.FACETS));
    }
    if (searchQueryMap.containsKey(JsonKey.FIELDS)) {
      search.setFields((List<String>) searchQueryMap.get(JsonKey.FIELDS));
    }
    if (searchQueryMap.containsKey(JsonKey.FILTERS)) {
      search.getAdditionalProperties().put(JsonKey.FILTERS, searchQueryMap.get(JsonKey.FILTERS));
    }
    if (searchQueryMap.containsKey(JsonKey.EXISTS)) {
      search.getAdditionalProperties().put(JsonKey.EXISTS, searchQueryMap.get(JsonKey.EXISTS));
    }
    if (searchQueryMap.containsKey(JsonKey.NOT_EXISTS)) {
      search
          .getAdditionalProperties()
          .put(JsonKey.NOT_EXISTS, searchQueryMap.get(JsonKey.NOT_EXISTS));
    }
    if (searchQueryMap.containsKey(JsonKey.SORT_BY)) {
      search
          .getSortBy()
          .putAll((Map<? extends String, ? extends String>) searchQueryMap.get(JsonKey.SORT_BY));
    }
    return search;
  }

  /**
   * Processes Elasticsearch search response and creates a result map.
   *
   * @param response the SearchResponse from Elasticsearch
   * @param searchDTO the SearchDTO used for the query
   * @param finalFacetList list to populate with facet results
   * @return map containing search results, facets, and count
   */
  public static Map<String, Object> getSearchResponseMap(
      SearchResponse response, SearchDTO searchDTO, List finalFacetList) {
    Map<String, Object> responseMap = new HashMap<>();
    List<Map<String, Object>> esSource = new ArrayList<>();
    long count = 0;
    if (response != null) {
      SearchHits hits = response.getHits();
      count = hits.getTotalHits().value;

      for (SearchHit hit : hits) {
        esSource.add(hit.getSourceAsMap());
      }

      // fetch aggregations aggregations
      finalFacetList = getFinalFacetList(response, searchDTO, finalFacetList);
    }
    responseMap.put(JsonKey.CONTENT, esSource);
    if (!(finalFacetList.isEmpty())) {
      responseMap.put(JsonKey.FACETS, finalFacetList);
    }
    responseMap.put(JsonKey.COUNT, count);
    return responseMap;
  }

  /**
   * Extracts and processes facet aggregations from the search response.
   *
   * @param response the SearchResponse from Elasticsearch
   * @param searchDTO the SearchDTO containing facet definitions
   * @param finalFacetList list to populate with facet results
   * @return list of facet results with names, values, and counts
   */
  private static List getFinalFacetList(
      SearchResponse response, SearchDTO searchDTO, List finalFacetList) {
    if (null != searchDTO.getFacets() && !searchDTO.getFacets().isEmpty()) {
      Map<String, String> m1 = searchDTO.getFacets().get(0);
      for (Map.Entry<String, String> entry : m1.entrySet()) {
        String field = entry.getKey();
        String aggsType = entry.getValue();
        List<Object> aggsList = new ArrayList<>();
        Map facetMap = new HashMap();
        if (JsonKey.DATE_HISTOGRAM.equalsIgnoreCase(aggsType)) {
          Histogram agg = response.getAggregations().get(field);
          for (Histogram.Bucket ent : agg.getBuckets()) {
            // DateTime key = (DateTime) ent.getKey(); // Key
            String keyAsString = ent.getKeyAsString(); // Key as String
            long docCount = ent.getDocCount(); // Doc count
            Map internalMap = new HashMap();
            internalMap.put(JsonKey.NAME, keyAsString);
            internalMap.put(JsonKey.COUNT, docCount);
            aggsList.add(internalMap);
          }
        } else {
          Terms aggs = response.getAggregations().get(field);
          for (Bucket bucket : aggs.getBuckets()) {
            Map internalMap = new HashMap();
            internalMap.put(JsonKey.NAME, bucket.getKey());
            internalMap.put(JsonKey.COUNT, bucket.getDocCount());
            aggsList.add(internalMap);
          }
        }
        facetMap.put("values", aggsList);
        facetMap.put(JsonKey.NAME, field);
        finalFacetList.add(facetMap);
      }
    }
    return finalFacetList;
  }
}

