package org.sunbird.common;

/**
 * Constants interface for Cassandra database operations and configuration.
 * 
 * <p>Provides constants for:
 * <ul>
 *   <li>Cassandra connection pool configuration properties</li>
 *   <li>Domain entity identifiers (course, user, content)</li>
 *   <li>SQL query keywords and clauses</li>
 *   <li>Operator symbols for WHERE conditions</li>
 *   <li>Exception and error messages</li>
 * </ul>
 */
public interface Constants {

  // ============================================================================
  // CASSANDRA CONNECTION POOL CONFIGURATION PROPERTIES
  // ============================================================================

  /** Core connections per host for local Cassandra connections. */
  String CORE_CONNECTIONS_PER_HOST_FOR_LOCAL = "coreConnectionsPerHostForLocal";

  /** Core connections per host for remote Cassandra connections. */
  String CORE_CONNECTIONS_PER_HOST_FOR_REMOTE = "coreConnectionsPerHostForRemote";

  /** Maximum connections per host for local Cassandra connections. */
  String MAX_CONNECTIONS_PER_HOST_FOR_LOCAl = "maxConnectionsPerHostForLocal";

  /** Maximum connections per host for remote Cassandra connections. */
  String MAX_CONNECTIONS_PER_HOST_FOR_REMOTE = "maxConnectionsPerHostForRemote";

  /** Maximum number of requests allowed per connection. */
  String MAX_REQUEST_PER_CONNECTION = "maxRequestsPerConnection";

  /** Heartbeat interval in seconds to maintain connection health. */
  String HEARTBEAT_INTERVAL = "heartbeatIntervalSeconds";

  /** Connection pool timeout in milliseconds. */
  String POOL_TIMEOUT = "poolTimeoutMillis";

  /** Cassandra contact point (host address). */
  String CONTACT_POINT = "contactPoint";

  /** Cassandra server port number. */
  String PORT = "port";

  /** Query logger threshold constant. */
  String QUERY_LOGGER_THRESHOLD = "queryLoggerConstantThreshold";

  /** Cassandra configuration properties file name. */
  String CASSANDRA_PROPERTIES_FILE = "cassandra.config.properties";

  // ============================================================================
  // DOMAIN ENTITY IDENTIFIERS
  // ============================================================================

  /** Course entity identifier. */
  String COURSE_ID = "courseId";

  /** User entity identifier. */
  String USER_ID = "userId";

  /** Content entity identifier. */
  String CONTENT_ID = "contentId";

  /** Generic record identifier or primary key. */
  String IDENTIFIER = "id";

  /** Alternate identifier field name. */
  String ID = "id";

  // ============================================================================
  // OPERATION STATUS AND RESPONSE CONSTANTS
  // ============================================================================

  /** Indicates successful operation. */
  String SUCCESS = "SUCCESS";

  /** Response data field key. */
  String RESPONSE = "response";

  /** Multi-data center enabled flag. */
  String IS_MULTI_DC_ENABLED = "isMultiDCEnabled";

  // ============================================================================
  // SQL QUERY KEYWORDS AND CLAUSES
  // ============================================================================

  /** INSERT INTO SQL keyword. */
  String INSERT_INTO = "INSERT INTO ";

  /** SELECT SQL keyword. */
  String SELECT = "SELECT ";

  /** UPDATE SQL keyword. */
  String UPDATE = "UPDATE ";

  /** FROM SQL keyword with spacing. */
  String FROM = " FROM ";

  /** WHERE SQL keyword with spacing. */
  String WHERE = " where ";

  /** SET SQL keyword with spacing. */
  String SET = " SET ";

  /** IF EXISTS SQL clause with spacing and semicolon. */
  String IF_EXISTS = " IF EXISTS;";

  /** IF NOT EXISTS SQL clause with spacing and semicolon. */
  String IF_NOT_EXISTS = " IF NOT EXISTS;";

  // ============================================================================
  // QUERY PUNCTUATION AND DELIMITERS
  // ============================================================================

  /** Opening parenthesis. */
  String OPEN_BRACE = "(";

  /** Opening parenthesis with trailing space. */
  String OPEN_BRACE_WITH_SPACE = " (";

  /** VALUES clause for INSERT statements. */
  String VALUES_WITH_BRACE = ") VALUES (";

  /** Closing parenthesis with semicolon. */
  String CLOSING_BRACE = ");";

  /** Comma separator. */
  String COMMA = ",";

  /** Comma with trailing space. */
  String COMMA_WITH_SPACE = ", ";

  /** Comma with closing parenthesis. */
  String COMMA_BRAC = "),";

  /** Dot separator for keyspace.table notation. */
  String DOT = ".";

  /** Question mark for parameterized queries. */
  String QUE_MARK = "?";

  /** Semicolon statement terminator. */
  String SEMICOLON = ";";

  // ============================================================================
  // COMPARISON OPERATORS FOR WHERE CONDITIONS
  // ============================================================================

  /** Equal to operator (=). */
  String EQUAL = " = ";

  /** Less than or equal to operator (<=). */
  String LTE = "<=";

  /** Less than operator (<). */
  String LT = "<";

  /** Greater than or equal to operator (>=). */
  String GTE = ">=";

  /** Greater than operator (>). */
  String GT = ">";

  /** Equal with question mark for parameterized query. */
  String EQUAL_WITH_QUE_MARK = " = ? ";

  /** WHERE id clause. */
  String WHERE_ID = "where id";

  // ============================================================================
  // ERROR AND EXCEPTION MESSAGES
  // ============================================================================

  /** Generic error message for incorrect data. */
  String INCORRECT_DATA = "Incorrect Data";

  /** Cassandra session is null error message. */
  String SESSION_IS_NULL = "cassandra session is null for this ";

  /** Cassandra cluster is null error message. */
  String CLUSTER_IS_NULL = "cassandra cluster value is null for this ";

  /** Record already exists error message. */
  String ALREADY_EXIST = "Record with this primary key already exist.";

  /** Exception message prefix for fetch operations. */
  String EXCEPTION_MSG_FETCH = "Exception occurred while fetching record from ";

  /** Exception message prefix for upsert operations. */
  String EXCEPTION_MSG_UPSERT = "Exception occured while upserting record from ";

  /** Exception message prefix for delete operations. */
  String EXCEPTION_MSG_DELETE = "Exception occurred while deleting record from ";

  /** Exception message prefix for update operations. */
  String EXCEPTION_MSG_UPDATE = "Exception occurred while updating record to ";
}

