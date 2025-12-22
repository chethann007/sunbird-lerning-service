package org.sunbird.cassandraimpl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.sunbird.common.CassandraUtil;
import org.sunbird.common.Constants;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.request.RequestContext;
import org.sunbird.response.Response;

/**
 * Cassandra Data Access implementation with advanced query capabilities.
 * 
 * <p>Extends CassandraOperationImpl to provide additional functionality for:
 * <ul>
 *   <li>Flexible record filtering and retrieval</li>
 *   <li>Asynchronous query execution with callbacks</li>
 *   <li>Map column add/remove operations</li>
 * </ul>
 */
public class CassandraDACImpl extends CassandraOperationImpl {
  private final LoggerUtil logger = new LoggerUtil(CassandraDACImpl.class);

  /**
   * Retrieves records with flexible filtering and column selection.
   * 
   * <p>Supports filtering by multiple fields and selecting specific columns.
   * Filters support both equality and IN (list) operators.
   *
   * @param keySpace The Cassandra keyspace name
   * @param table The table name
   * @param filters Map of column filters (supports List values for IN queries)
   * @param fields List of columns to return (null for all columns)
   * @param context The request context
   * @return Response containing matched records
   * @throws ProjectCommonException If query execution fails
   */
  public Response getRecords(
      String keySpace,
      String table,
      Map<String, Object> filters,
      List<String> fields,
      RequestContext context) {
    long startTime = System.currentTimeMillis();
    Response response;
    Session session = connectionManager.getSession(keySpace);
    Select select = null;
    try {
      if (CollectionUtils.isNotEmpty(fields)) {
        select = QueryBuilder.select((String[]) fields.toArray()).from(keySpace, table);
      } else {
        select = QueryBuilder.select().all().from(keySpace, table);
      }

      if (MapUtils.isNotEmpty(filters)) {
        Select.Where where = select.where();
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
          Object value = filter.getValue();
          if (value instanceof List) {
            where = where.and(QueryBuilder.in(filter.getKey(), ((List) filter.getValue())));
          } else {
            where = where.and(QueryBuilder.eq(filter.getKey(), filter.getValue()));
          }
        }
      }

      ResultSet results = null;
      results = session.execute(select);
      response = CassandraUtil.createResponse(results);
    } catch (Exception e) {
      logger.error(context, Constants.EXCEPTION_MSG_FETCH + table + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.serverError,
          ResponseCode.serverError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    } finally {
      if (null != select) {
        logQueryElapseTime("getRecords", startTime, select.getQueryString(), context);
      }
    }
    return response;
  }

  /**
   * Executes asynchronous query with callback processing.
   * 
   * <p>Runs a SELECT query asynchronously and applies the provided callback
   * to the ResultSet when ready. Uses a fixed thread pool for callback execution.
   *
   * @param keySpace The Cassandra keyspace name
   * @param table The table name
   * @param filters Map of column filters (supports List values for IN queries)
   * @param fields List of columns to return (null for all columns)
   * @param callback Callback to process the ResultSet
   * @param context The request context
   * @throws ProjectCommonException If query execution fails
   */
  public void applyOperationOnRecordsAsync(
      String keySpace,
      String table,
      Map<String, Object> filters,
      List<String> fields,
      FutureCallback<ResultSet> callback,
      RequestContext context) {
    long startTime = System.currentTimeMillis();
    Session session = connectionManager.getSession(keySpace);
    Select select = null;
    try {
      if (CollectionUtils.isNotEmpty(fields)) {
        select = QueryBuilder.select((String[]) fields.toArray()).from(keySpace, table);
      } else {
        select = QueryBuilder.select().all().from(keySpace, table);
      }

      if (MapUtils.isNotEmpty(filters)) {
        Select.Where where = select.where();
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
          Object value = filter.getValue();
          if (value instanceof List) {
            where = where.and(QueryBuilder.in(filter.getKey(), ((List) filter.getValue())));
          } else {
            where = where.and(QueryBuilder.eq(filter.getKey(), filter.getValue()));
          }
        }
      }
      ResultSetFuture future = session.executeAsync(select);
      Futures.addCallback(future, callback, Executors.newFixedThreadPool(1));
    } catch (Exception e) {
      logger.error(context, Constants.EXCEPTION_MSG_FETCH + table + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.serverError,
          ResponseCode.serverError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    } finally {
      if (null != select) {
        logQueryElapseTime(
            "applyOperationOnRecordsAsync", startTime, select.getQueryString(), context);
      }
    }
  }

  /**
   * Adds a key-value pair to a map column in a record.
   *
   * @param keySpace The Cassandra keyspace name
   * @param table The table name
   * @param primaryKey Map representing the primary key of the record
   * @param column The map column name
   * @param key The key to add to the map
   * @param value The value to associate with the key
   * @param context The request context
   * @return Response indicating operation status
   * @throws ProjectCommonException If query execution fails
   */
  public Response updateAddMapRecord(
      String keySpace,
      String table,
      Map<String, Object> primaryKey,
      String column,
      String key,
      Object value,
      RequestContext context) {
    return updateMapRecord(keySpace, table, primaryKey, column, key, value, true, context);
  }

  /**
   * Removes a key from a map column in a record.
   *
   * @param keySpace The Cassandra keyspace name
   * @param table The table name
   * @param primaryKey Map representing the primary key of the record
   * @param column The map column name
   * @param key The key to remove from the map
   * @param context The request context
   * @return Response indicating operation status
   * @throws ProjectCommonException If query execution fails
   */
  public Response updateRemoveMapRecord(
      String keySpace,
      String table,
      Map<String, Object> primaryKey,
      String column,
      String key,
      RequestContext context) {
    return updateMapRecord(keySpace, table, primaryKey, column, key, null, false, context);
  }

  /**
   * Helper method for adding/removing map column entries.
   * 
   * <p>Performs PUT or REMOVE operations on map columns based on the 'add' flag.
   *
   * @param keySpace The Cassandra keyspace name
   * @param table The table name
   * @param primaryKey Map representing the primary key of the record
   * @param column The map column name
   * @param key The key to add or remove
   * @param value The value for PUT operations (ignored for REMOVE)
   * @param add true to add key-value pair; false to remove key
   * @param context The request context
   * @return Response indicating operation status
   * @throws ProjectCommonException If primary key is empty or query execution fails
   */
  public Response updateMapRecord(
      String keySpace,
      String table,
      Map<String, Object> primaryKey,
      String column,
      String key,
      Object value,
      boolean add,
      RequestContext context) {
    long startTime = System.currentTimeMillis();
    Update update = QueryBuilder.update(keySpace, table);
    if (add) {
      update.with(QueryBuilder.put(column, key, value));
    } else {
      update.with(QueryBuilder.remove(column, key));
    }
    if (MapUtils.isEmpty(primaryKey)) {
      logger.info(
          context,
          Constants.EXCEPTION_MSG_FETCH + table + " : primary key is a must for update call");
      throw new ProjectCommonException(
          ResponseCode.serverError,
          ResponseCode.serverError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    Update.Where where = update.where();
    for (Map.Entry<String, Object> filter : primaryKey.entrySet()) {
      Object filterValue = filter.getValue();
      if (filterValue instanceof List) {
        where = where.and(QueryBuilder.in(filter.getKey(), ((List) filter.getValue())));
      } else {
        where = where.and(QueryBuilder.eq(filter.getKey(), filter.getValue()));
      }
    }
    try {
      Response response = new Response();
      connectionManager.getSession(keySpace).execute(update);
      response.put(Constants.RESPONSE, Constants.SUCCESS);
      return response;
    } catch (Exception e) {
      logger.error(context, Constants.EXCEPTION_MSG_FETCH + table + " : " + e.getMessage(), e);
      throw new ProjectCommonException(
          ResponseCode.serverError,
          ResponseCode.serverError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    } finally {
      if (null != update) {
        logQueryElapseTime("updateMapRecord", startTime, update.getQueryString(), context);
      }
    }
  }
}

