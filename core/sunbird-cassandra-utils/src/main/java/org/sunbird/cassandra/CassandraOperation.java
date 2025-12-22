package org.sunbird.cassandra;

import com.datastax.driver.core.ResultSet;
import com.google.common.util.concurrent.FutureCallback;
import java.util.List;
import java.util.Map;
import org.sunbird.request.RequestContext;
import org.sunbird.response.Response;

/**
 * Interface for Cassandra database operations.
 * 
 * <p>Provides CRUD operations, batch processing, TTL support, and composite key handling
 * for interacting with Cassandra tables.
 */
public interface CassandraOperation {

  /**
   * Inserts or updates a record in Cassandra (upsert operation).
   * 
   * <p>If the primary key exists, updates the record; otherwise, inserts a new record.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param request Column name-value map
   * @param context Request context
   * @return Response indicating operation status
   */
  Response upsertRecord(
      String keyspaceName, String tableName, Map<String, Object> request, RequestContext context);

  /**
   * Inserts a new record into Cassandra.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param request Column name-value map
   * @param context Request context
   * @return Response indicating operation status
   */
  Response insertRecord(
      String keyspaceName, String tableName, Map<String, Object> request, RequestContext context);

  /**
   * Updates an existing record in Cassandra.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param request Column name-value map
   * @param context Request context
   * @return Response indicating operation status
   */
  Response updateRecord(
      String keyspaceName, String tableName, Map<String, Object> request, RequestContext context);

  /**
   * Deletes a record by primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param identifier Primary key value
   * @param context Request context
   * @return Response indicating operation status
   */
  Response deleteRecord(
      String keyspaceName, String tableName, String identifier, RequestContext context);

  /**
   * Deletes a record by composite primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param compositeKeyMap Composite primary key column map
   * @param context Request context
   */
  void deleteRecord(
      String keyspaceName,
      String tableName,
      Map<String, String> compositeKeyMap,
      RequestContext context);

  /**
   * Deletes multiple records by primary keys.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param identifierList List of primary key values
   * @param context Request context
   * @return true if operation succeeded
   */
  boolean deleteRecords(
      String keyspaceName, String tableName, List<String> identifierList, RequestContext context);

  /**
   * Fetches records using IN query on a single property.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param propertyName Column name
   * @param propertyValueList List of values for IN clause
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecordsByProperty(
      String keyspaceName,
      String tableName,
      String propertyName,
      List<Object> propertyValueList,
      RequestContext context);

  /**
   * Fetches records with specified columns matching given property map.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param propertyMap Column name-value pairs for WHERE clause
   * @param fields Columns to return (null for all)
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecordsByProperties(
      String keyspaceName,
      String tableName,
      Map<String, Object> propertyMap,
      List<String> fields,
      RequestContext context);

  /**
   * Fetches records matching given property map without ALLOW FILTERING.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param propertyMap Column name-value pairs for WHERE clause
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecordsByProperties(
      String keyspaceName,
      String tableName,
      Map<String, Object> propertyMap,
      RequestContext context);

  /**
   * Fetches specific properties for a single record by ID.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param id Primary key value
   * @param properties Columns to return
   * @param context Request context
   * @return Response containing property values
   */
  Response getPropertiesValueById(
      String keyspaceName,
      String tableName,
      String id,
      List<String> properties,
      RequestContext context);

  /**
   * Fetches specific properties for multiple records by IDs.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param ids List of primary key values
   * @param properties Columns to return
   * @param context Request context
   * @return Response containing property values
   */
  Response getPropertiesValueById(
      String keyspaceName,
      String tableName,
      List<String> ids,
      List<String> properties,
      RequestContext context);

  /**
   * Fetches all records from a table.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param context Request context
   * @return Response containing all records
   */
  Response getAllRecords(String keyspaceName, String tableName, RequestContext context);

  /**
   * Fetches all records with specified columns from a table.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param fields Columns to return
   * @param context Request context
   * @return Response containing all records
   */
  Response getAllRecords(
      String keyspaceName, String tableName, List<String> fields, RequestContext context);

  /**
   * Updates a record using composite primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param updateAttributes Columns to update with new values
   * @param compositeKey Composite primary key column map
   * @param context Request context
   * @return Response indicating operation status
   */
  Response updateRecord(
      String keyspaceName,
      String tableName,
      Map<String, Object> updateAttributes,
      Map<String, Object> compositeKey,
      RequestContext context);

  /**
   * Fetches a record by primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param key Primary key value
   * @param context Request context
   * @return Response containing matched record
   */
  Response getRecordById(String keyspaceName, String tableName, String key, RequestContext context);

  /**
   * Fetches a record by composite primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param key Composite primary key column map
   * @param context Request context
   * @return Response containing matched record
   */
  Response getRecordById(
      String keyspaceName, String tableName, Map<String, Object> key, RequestContext context);

  /**
   * Fetches a record with specified columns by primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param key Primary key value
   * @param fields Columns to return (null for all)
   * @param context Request context
   * @return Response containing matched record
   */
  Response getRecordById(
      String keyspaceName,
      String tableName,
      String key,
      List<String> fields,
      RequestContext context);

  /**
   * Fetches a record with specified columns by composite primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param key Composite primary key column map
   * @param fields Columns to return (null for all)
   * @param context Request context
   * @return Response containing matched record
   */
  Response getRecordById(
      String keyspaceName,
      String tableName,
      Map<String, Object> key,
      List<String> fields,
      RequestContext context);

  /**
   * Fetches a record with specified columns and TTL values by composite primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param key Composite primary key column map
   * @param ttlFields Columns to include TTL values for
   * @param fields Columns to return (null for all)
   * @param context Request context
   * @return Response containing matched record with TTL
   */
  Response getRecordWithTTLById(
      String keyspaceName,
      String tableName,
      Map<String, Object> key,
      List<String> ttlFields,
      List<String> fields,
      RequestContext context);

  /**
   * Performs batch insert operation.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param records List of records to insert
   * @param context Request context
   * @return Response indicating operation status
   */
  Response batchInsert(
      String keyspaceName,
      String tableName,
      List<Map<String, Object>> records,
      RequestContext context);

  /**
   * Performs batch update operation.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param records List of maps with PK (primary key) and NonPK (update values) entries
   * @param context Request context
   * @return Response indicating operation status
   */
  Response batchUpdate(
      String keyspaceName,
      String tableName,
      List<Map<String, Map<String, Object>>> records,
      RequestContext context);

  /**
   * Performs batch update by ID.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param records List of records to update
   * @param context Request context
   * @return Response indicating operation status
   */
  Response batchUpdateById(
      String keyspaceName,
      String tableName,
      List<Map<String, Object>> records,
      RequestContext context);

  /**
   * Fetches records by composite key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param compositeKeyMap Composite primary key column map
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecordsByCompositeKey(
      String keyspaceName,
      String tableName,
      Map<String, Object> compositeKeyMap,
      RequestContext context);

  /**
   * Fetches specified columns for records matching given IDs.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param properties Columns to return
   * @param ids List of primary key values
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecordsByIdsWithSpecifiedColumns(
      String keyspaceName,
      String tableName,
      List<String> properties,
      List<String> ids,
      RequestContext context);

  /**
   * Fetches records by primary keys.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param primaryKeys List of primary key values
   * @param primaryKeyColumnName Primary key column name
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecordsByPrimaryKeys(
      String keyspaceName,
      String tableName,
      List<String> primaryKeys,
      String primaryKeyColumnName,
      RequestContext context);

  /**
   * Inserts a record with TTL expiration.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param request Column name-value map
   * @param ttl Time to live in seconds
   * @param context Request context
   * @return Response indicating operation status
   */
  Response insertRecordWithTTL(
      String keyspaceName,
      String tableName,
      Map<String, Object> request,
      int ttl,
      RequestContext context);

  /**
   * Updates a record with TTL expiration.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param request Column name-value map to update
   * @param compositeKey Composite primary key column map
   * @param ttl Time to live in seconds
   * @param context Request context
   * @return Response indicating operation status
   */
  Response updateRecordWithTTL(
      String keyspaceName,
      String tableName,
      Map<String, Object> request,
      Map<String, Object> compositeKey,
      int ttl,
      RequestContext context);
  /**
   * Fetches specified columns with TTL values for records matching partition/primary key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param primaryKeys Partition/primary key column map
   * @param properties Columns to return
   * @param ttlPropertiesWithAlias TTL column to alias mapping
   * @param context Request context
   * @return Response containing matched records with TTL
   */
  Response getRecordsByIdsWithSpecifiedColumnsAndTTL(
      String keyspaceName,
      String tableName,
      Map<String, Object> primaryKeys,
      List<String> properties,
      Map<String, String> ttlPropertiesWithAlias,
      RequestContext context);

  /**
   * Performs batch insert with individual TTL values per record.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param records List of records to insert
   * @param ttls TTL in seconds for each record (ignored if not positive)
   * @param context Request context
   * @return Response indicating operation status
   */
  Response batchInsertWithTTL(
      String keyspaceName,
      String tableName,
      List<Map<String, Object>> records,
      List<Integer> ttls,
      RequestContext context);

  /**
   * Fetches records matching filters with specified columns.
   *
   * @param keyspace Keyspace name
   * @param table Table name
   * @param filters Column name-value pairs for filtering
   * @param fields Columns to return
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecords(
      String keyspace,
      String table,
      Map<String, Object> filters,
      List<String> fields,
      RequestContext context);

  /**
   * Applies a callback on asynchronous Cassandra read operation.
   *
   * @param keySpace Keyspace name
   * @param table Table name
   * @param filters Column name-value pairs for filtering
   * @param fields Columns to return
   * @param callback Callback to apply on ResultSet
   * @param context Request context
   */
  void applyOperationOnRecordsAsync(
      String keySpace,
      String table,
      Map<String, Object> filters,
      List<String> fields,
      FutureCallback<ResultSet> callback,
      RequestContext context);

  /**
   * Performs a batch action.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param inputData Input data for batch operation
   * @param context Request context
   * @return Response indicating operation status
   */
  Response performBatchAction(
      String keyspaceName, String tableName, Map<String, Object> inputData, RequestContext context);

  /**
   * Searches for a value in a list column using CONTAINS query.
   *
   * @param keyspace Keyspace name
   * @param tableName Table name
   * @param key Column name containing list
   * @param value Value to search for
   * @param context Request context
   * @return Response containing matched records
   */
  Response searchValueInList(
      String keyspace, String tableName, String key, String value, RequestContext context);

  /**
   * Searches for a value in a list column with additional filters.
   *
   * @param keyspace Keyspace name
   * @param tableName Table name
   * @param key Column name containing list
   * @param value Value to search for
   * @param propertyMap Additional filter conditions
   * @param context Request context
   * @return Response containing matched records
   */
  Response searchValueInList(
      String keyspace,
      String tableName,
      String key,
      String value,
      Map<String, Object> propertyMap,
      RequestContext context);

  /**
   * Adds a key-value pair to a map column.
   *
   * @param keySpace Keyspace name
   * @param table Table name
   * @param primaryKey Primary key column map
   * @param column Map column name
   * @param key Key to add to map
   * @param value Value to add to map
   * @param context Request context
   * @return Response indicating operation status
   */
  Response updateAddMapRecord(
      String keySpace,
      String table,
      Map<String, Object> primaryKey,
      String column,
      String key,
      Object value,
      RequestContext context);

  /**
   * Removes a key from a map column.
   *
   * @param keySpace Keyspace name
   * @param table Table name
   * @param primaryKey Primary key column map
   * @param column Map column name
   * @param key Key to remove from map
   * @param context Request context
   * @return Response indicating operation status
   */
  Response updateRemoveMapRecord(
      String keySpace,
      String table,
      Map<String, Object> primaryKey,
      String column,
      String key,
      RequestContext context);

  /**
   * Fetches records by composite partition key.
   *
   * @param keyspaceName Keyspace name
   * @param tableName Table name
   * @param partitionKeyMap Partition key column map
   * @param context Request context
   * @return Response containing matched records
   */
  Response getRecordsByCompositePartitionKey(
      String keyspaceName,
      String tableName,
      Map<String, Object> partitionKeyMap,
      RequestContext context);
}

