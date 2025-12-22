package org.sunbird.common.inf;

import java.util.List;
import java.util.Map;
import org.sunbird.dto.SearchDTO;
import org.sunbird.request.RequestContext;
import scala.concurrent.Future;

/**
 * Service interface for Elasticsearch operations.
 *
 * <p>Defines asynchronous operations for managing documents in Elasticsearch including CRUD
 * operations, search, bulk operations, and health checks. All methods return Future objects for
 * non-blocking execution.
 */
public interface ElasticSearchService {
  public static final String _DOC = "_doc";

  /**
   * Saves a new document to Elasticsearch.
   *
   * <p>The identifier value becomes the document _id in Elasticsearch. Ensure unique identifier
   * values for each document.
   *
   * @param index the Elasticsearch index name
   * @param identifier the unique document identifier (becomes _id in ES)
   * @param data the document data as a Map
   * @param context the request context
   * @return Future containing the document identifier
   */
  public Future<String> save(
      String index, String identifier, Map<String, Object> data, RequestContext context);

  /**
   * Updates an existing document in Elasticsearch.
   *
   * <p>Retrieves the document by identifier, merges it with the provided data, and updates it.
   *
   * @param index the Elasticsearch index name
   * @param identifier the document identifier
   * @param data the data to merge with existing document
   * @param context the request context
   * @return Future containing boolean success status
   */
  public Future<Boolean> update(
      String index, String identifier, Map<String, Object> data, RequestContext context);

  /**
   * Retrieves a document from Elasticsearch by its identifier.
   *
   * @param index the Elasticsearch index name
   * @param identifier the document identifier
   * @param context the request context
   * @return Future containing the document data as a Map, or null if not found
   */
  public Future<Map<String, Object>> getDataByIdentifier(
      String index, String identifier, RequestContext context);

  /**
   * Deletes a document from Elasticsearch by its identifier.
   *
   * @param index the Elasticsearch index name
   * @param identifier the document identifier
   * @param context the request context
   * @return Future containing boolean success status
   */
  public Future<Boolean> delete(String index, String identifier, RequestContext context);

  /**
   * Performs an Elasticsearch search based on search criteria.
   *
   * <p>Uses SearchDTO to specify search parameters including fields, facets, sorting, filters,
   * and pagination.
   *
   * @param searchDTO the search criteria object
   * @param index the Elasticsearch index name
   * @param context the request context
   * @return Future containing the search results as a Map
   */
  public Future<Map<String, Object>> search(
      SearchDTO searchDTO, String index, RequestContext context);

  /**
   * Performs a health check on the Elasticsearch cluster.
   *
   * @return Future containing the health check result
   */
  public Future<Boolean> healthCheck();

  /**
   * Inserts multiple documents into Elasticsearch in bulk.
   *
   * @param index the Elasticsearch index name
   * @param dataList the list of documents to insert
   * @param context the request context
   * @return Future containing boolean success status
   */
  public Future<Boolean> bulkInsert(
      String index, List<Map<String, Object>> dataList, RequestContext context);

  /**
   * Inserts or updates a document in Elasticsearch.
   *
   * <p>If the document exists, it is updated by merging with the provided data. If it doesn't
   * exist, a new document is created.
   *
   * @param index the Elasticsearch index name
   * @param identifier the document identifier
   * @param data the document data
   * @param context the request context
   * @return Future containing boolean success status
   */
  public Future<Boolean> upsert(
      String index, String identifier, Map<String, Object> data, RequestContext context);

  /**
   * Retrieves multiple documents from Elasticsearch by a list of IDs.
   *
   * @param ids the list of document identifiers
   * @param fields the list of fields to retrieve from each document
   * @param index the Elasticsearch index name
   * @param context the request context
   * @return Future containing a Map with document IDs as keys and document data as values
   */
  public Future<Map<String, Map<String, Object>>> getEsResultByListOfIds(
      List<String> ids, List<String> fields, String index, RequestContext context);
}

