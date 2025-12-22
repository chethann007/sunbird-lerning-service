package org.sunbird.common;

import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.core.querybuilder.Update.Assignments;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.sunbird.cassandraannotation.ClusteringKey;
import org.sunbird.cassandraannotation.PartitioningKey;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.response.Response;
import org.sunbird.util.ProjectUtil;

/**
 * Utility class providing helper methods for Cassandra database operations.
 * 
 * <p>Provides functionality for:
 * <ul>
 *   <li>Query statement generation (INSERT, SELECT, UPDATE)</li>
 *   <li>ResultSet to Map conversion and column mapping</li>
 *   <li>Composite primary key handling</li>
 *   <li>Dynamic update query construction</li>
 *   <li>WHERE clause generation with filter operators</li>
 * </ul>
 * 
 * <p>This is a utility class with static methods only; instantiation is not allowed.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class CassandraUtil {
  private static final LoggerUtil logger = new LoggerUtil(CassandraUtil.class);

  private static final CassandraPropertyReader propertiesCache =
      CassandraPropertyReader.getInstance();
  private static final String SERIAL_VERSION_UID = "serialVersionUID";

  private CassandraUtil() {}

  /**
   * Creates an INSERT prepared statement based on the provided column map.
   *
   * @param keyspaceName The Cassandra keyspace name
   * @param tableName The table name
   * @param map Column name-value map for INSERT statement
   * @return Formatted INSERT statement string
   */
  public static String getPreparedStatement(
      String keyspaceName, String tableName, Map<String, Object> map) {
    StringBuilder query = new StringBuilder();
    query.append(
        Constants.INSERT_INTO + keyspaceName + Constants.DOT + tableName + Constants.OPEN_BRACE);
    Set<String> keySet = map.keySet();
    query.append(String.join(",", keySet) + Constants.VALUES_WITH_BRACE);
    StringBuilder commaSepValueBuilder = new StringBuilder();
    for (int i = 0; i < keySet.size(); i++) {
      commaSepValueBuilder.append(Constants.QUE_MARK);
      if (i != keySet.size() - 1) {
        commaSepValueBuilder.append(Constants.COMMA);
      }
    }
    query.append(commaSepValueBuilder + Constants.CLOSING_BRACE);
    return query.toString();
  }

  /**
   * Converts a Cassandra ResultSet into a Response object.
   * 
   * <p>Maps each row in the ResultSet to a Map&lt;String, Object&gt; using column mappings
   * from the properties cache, and returns a Response containing the list of maps.
   *
   * @param results The Cassandra ResultSet to convert
   * @return Response object with the key "response" containing a list of row maps
   */
  public static Response createResponse(ResultSet results) {
    Response response = new Response();
    List<Map<String, Object>> responseList = new ArrayList<>();
    Map<String, String> columnsMapping = fetchColumnsMapping(results);
    Iterator<Row> rowIterator = results.iterator();
    rowIterator.forEachRemaining(
        row -> {
          Map<String, Object> rowMap = new HashMap<>();
          columnsMapping
              .entrySet()
              .stream()
              .forEach(entry -> rowMap.put(entry.getKey(), row.getObject(entry.getValue())));
          responseList.add(rowMap);
        });
    response.put(Constants.RESPONSE, responseList);
    return response;
  }

  public static Map<String, String> fetchColumnsMapping(ResultSet results) {
    return results
        .getColumnDefinitions()
        .asList()
        .stream()
        .collect(
            Collectors.toMap(
                d -> propertiesCache.readProperty(d.getName()).trim(), d -> d.getName()));
  }

  /**
   * Creates an UPDATE prepared statement based on the provided column map.
   *
   * @param keyspaceName The Cassandra keyspace name
   * @param tableName The table name
   * @param map Column name-value map (excludes IDENTIFIER key which is used for WHERE clause)
   * @return Formatted UPDATE statement string with SET clause and WHERE condition
   */
  public static String getUpdateQueryStatement(
      String keyspaceName, String tableName, Map<String, Object> map) {
    StringBuilder query =
        new StringBuilder(
            Constants.UPDATE + keyspaceName + Constants.DOT + tableName + Constants.SET);
    Set<String> key = new HashSet<>(map.keySet());
    key.remove(Constants.IDENTIFIER);
    query.append(String.join(" = ? ,", key));
    query.append(
        Constants.EQUAL_WITH_QUE_MARK + Constants.WHERE_ID + Constants.EQUAL_WITH_QUE_MARK);
    return query.toString();
  }

  /**
   * Creates a SELECT prepared statement for specified columns.
   *
   * @param keyspaceName The Cassandra keyspace name
   * @param tableName The table name
   * @param properties List of column names to select
   * @return Formatted SELECT statement string with WHERE clause on IDENTIFIER
   */
  public static String getSelectStatement(
      String keyspaceName, String tableName, List<String> properties) {
    StringBuilder query = new StringBuilder(Constants.SELECT);
    query.append(String.join(",", properties));
    query.append(
        Constants.FROM
            + keyspaceName
            + Constants.DOT
            + tableName
            + Constants.WHERE
            + Constants.IDENTIFIER
            + Constants.EQUAL
            + " ?; ");
    return query.toString();
  }

  /**
   * Processes exceptions for unknown identifier errors.
   * 
   * <p>Removes "unknown identifier" and "undefined identifier" prefixes from the exception
   * message and formats it using the invalidPropertyError response code.
   *
   * @param e The exception containing the error message to process
   * @return Formatted error message with identifier prefixes removed
   */
  public static String processExceptionForUnknownIdentifier(Exception e) {
    return ProjectUtil.formatMessage(
            ResponseCode.invalidPropertyError.getErrorMessage(),
            e.getMessage()
                .replace(JsonKey.UNKNOWN_IDENTIFIER, "")
                .replace(JsonKey.UNDEFINED_IDENTIFIER, ""))
        .trim();
  }

  /**
   * Separates an object's fields into primary key and non-primary key components.
   * 
   * <p>Analyzes the provided object's fields using {@link PartitioningKey} and
   * {@link ClusteringKey} annotations to categorize them. Returns a map with two submaps:
   * one for primary key attributes and one for updatable attributes.
   *
   * @param <T> The type of the object being analyzed
   * @param clazz The object instance (model class corresponding to Cassandra table)
   * @return Map with two entries: "primaryKey" containing PK fields and "nonPrimaryKey"
   *         containing updatable fields
   * @throws ProjectCommonException If field reflection fails
   */
  public static <T> Map<String, Map<String, Object>> batchUpdateQuery(T clazz) {
    Field[] fieldList = clazz.getClass().getDeclaredFields();

    Map<String, Object> primaryKeyMap = new HashMap<>();
    Map<String, Object> nonPKMap = new HashMap<>();
    try {
      for (Field field : fieldList) {
        String fieldName = null;
        Object fieldValue = null;
        Boolean isFieldPrimaryKeyPart = false;
        if (Modifier.isPrivate(field.getModifiers())) {
          field.setAccessible(true);
        }
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
          if (annotation instanceof PartitioningKey) {
            isFieldPrimaryKeyPart = true;
          } else if (annotation instanceof ClusteringKey) {
            isFieldPrimaryKeyPart = true;
          }
        }
        fieldName = field.getName();
        fieldValue = field.get(clazz);
        if (!(fieldName.equalsIgnoreCase(SERIAL_VERSION_UID))) {
          if (isFieldPrimaryKeyPart) {
            primaryKeyMap.put(fieldName, fieldValue);
          } else {
            nonPKMap.put(fieldName, fieldValue);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception occurred - batchUpdateQuery", ex);
      throw new ProjectCommonException(
          ResponseCode.serverError,
          ResponseCode.serverError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    Map<String, Map<String, Object>> map = new HashMap<>();
    map.put(JsonKey.PRIMARY_KEY, primaryKeyMap);
    map.put(JsonKey.NON_PRIMARY_KEY, nonPKMap);
    return map;
  }

  /**
   * Extracts composite primary key attributes from an object.
   * 
   * <p>Uses reflection to identify fields annotated with {@link PartitioningKey} or
   * {@link ClusteringKey}, and returns their names and values.
   *
   * @param <T> The type of the object being analyzed
   * @param clazz The object instance (model class corresponding to Cassandra table)
   * @return Map containing primary key field names and values
   * @throws ProjectCommonException If field reflection fails
   */
  public static <T> Map<String, Object> getPrimaryKey(T clazz) {
    Field[] fieldList = clazz.getClass().getDeclaredFields();
    Map<String, Object> primaryKeyMap = new HashMap<>();

    try {
      for (Field field : fieldList) {
        String fieldName = null;
        Object fieldValue = null;
        Boolean isFieldPrimaryKeyPart = false;
        if (Modifier.isPrivate(field.getModifiers())) {
          field.setAccessible(true);
        }
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
          if (annotation instanceof PartitioningKey) {
            isFieldPrimaryKeyPart = true;
          } else if (annotation instanceof ClusteringKey) {
            isFieldPrimaryKeyPart = true;
          }
        }
        fieldName = field.getName();
        fieldValue = field.get(clazz);
        if (!(fieldName.equalsIgnoreCase(SERIAL_VERSION_UID))) {
          if (isFieldPrimaryKeyPart) {
            primaryKeyMap.put(fieldName, fieldValue);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception occurred - getPrimaryKey", ex);
      throw new ProjectCommonException(
          ResponseCode.serverError,
          ResponseCode.serverError.getErrorMessage(),
          ResponseCode.SERVER_ERROR.getResponseCode());
    }
    return primaryKeyMap;
  }

  /**
   * Builds WHERE clause conditions supporting multiple filter operators.
   * 
   * <p>Supports the following filter operators:
   * <ul>
   *   <li>LTE, LT, GTE, GT for range queries (when value is a Map)</li>
   *   <li>IN queries (when value is a List)</li>
   *   <li>Equality (for all other value types)</li>
   * </ul>
   *
   * @param key The column name for the WHERE condition
   * @param value The filter value(s): Map&lt;operator, value&gt;, List for IN clause, or single value
   * @param where The QueryBuilder WHERE clause to append conditions to
   */
  public static void createWhereQuery(String key, Object value, Where where) {
    if (value instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) value;
      map.entrySet()
          .stream()
          .forEach(
              x -> {
                if (Constants.LTE.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.lte(key, x.getValue()));
                } else if (Constants.LT.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.lt(key, x.getValue()));
                } else if (Constants.GTE.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.gte(key, x.getValue()));
                } else if (Constants.GT.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.gt(key, x.getValue()));
                }
              });
    } else if (value instanceof List) {
      where.and(QueryBuilder.in(key, (List) value));
    } else {
      where.and(QueryBuilder.eq(key, value));
    }
  }

  /**
   * Builds a Cassandra UPDATE statement using QueryBuilder.
   * 
   * <p>Constructs an UPDATE query with SET clauses for non-PK fields and WHERE clauses
   * for composite primary key matching.
   *
   * @param primaryKey Map representing the composite primary key (field name-value pairs)
   * @param nonPKRecord Map containing fields to update (field name-value pairs)
   * @param keyspaceName The Cassandra keyspace name
   * @param tableName The Cassandra table name
   * @return RegularStatement ready for execution
   */
  public static RegularStatement createUpdateQuery(
      Map<String, Object> primaryKey,
      Map<String, Object> nonPKRecord,
      String keyspaceName,
      String tableName) {

    Update update = QueryBuilder.update(keyspaceName, tableName);
    Assignments assignments = update.with();
    Update.Where where = update.where();
    nonPKRecord
        .entrySet()
        .stream()
        .forEach(
            x -> {
              assignments.and(QueryBuilder.set(x.getKey(), x.getValue()));
            });
    primaryKey
        .entrySet()
        .stream()
        .forEach(
            x -> {
              where.and(QueryBuilder.eq(x.getKey(), x.getValue()));
            });
    return where;
  }

  /**
   * Builds WHERE clause conditions supporting multiple filter operators.
   * 
   * <p>Identical to {@link #createWhereQuery(String, Object, Where)} but with alternative naming.
   * Supports range queries (LTE, LT, GTE, GT), IN clauses, and equality conditions.
   *
   * @param key The column name for the WHERE condition
   * @param value The filter value(s): Map&lt;operator, value&gt;, List for IN clause, or single value
   * @param where The QueryBuilder WHERE clause to append conditions to
   */
  public static void createQuery(String key, Object value, Where where) {
    if (value instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) value;
      map.entrySet()
          .stream()
          .forEach(
              x -> {
                if (Constants.LTE.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.lte(key, x.getValue()));
                } else if (Constants.LT.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.lt(key, x.getValue()));
                } else if (Constants.GTE.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.gte(key, x.getValue()));
                } else if (Constants.GT.equalsIgnoreCase(x.getKey())) {
                  where.and(QueryBuilder.gt(key, x.getValue()));
                }
              });
    } else if (value instanceof List) {
      where.and(QueryBuilder.in(key, (List) value));
    } else {
      where.and(QueryBuilder.eq(key, value));
    }
  }
}

