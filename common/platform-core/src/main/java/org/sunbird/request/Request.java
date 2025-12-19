package org.sunbird.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.util.ProjectUtil;

/**
 * Represents an HTTP API request with context, parameters, and metadata.
 * 
 * <p>This class encapsulates request data including:
 * <ul>
 *   <li>Request context and telemetry information</li>
 *   <li>Request parameters and metadata (id, version, timestamp)</li>
 *   <li>Request body as a flexible key-value map</li>
 *   <li>Manager and operation information</li>
 *   <li>Execution environment and timeout settings</li>
 * </ul>
 * 
 * <p>The class provides utilities for case conversion and timeout validation.
 * It implements {@link Serializable} for transmission and persistence.
 * 
 * @see RequestContext
 * @see RequestParams
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request implements Serializable {

  private static final long serialVersionUID = -2362783406031347676L;
  private static final Integer MIN_TIMEOUT = 0;
  private static final Integer MAX_TIMEOUT = 30;
  private static final int WAIT_TIME_VALUE = 30;

  /** Telemetry context data */
  protected Map<String, Object> context;

  /** Request-specific context information */
  private RequestContext requestContext;

  /** Request identifier */
  private String id;

  /** Request version */
  private String ver;

  /** Request timestamp */
  private String ts;

  /** Request parameters */
  private RequestParams params;

  /** Request body data as key-value pairs */
  private Map<String, Object> request = new WeakHashMap<>();

  /** Manager name handling this request */
  private String managerName;

  /** Operation to be performed */
  private String operation;

  /** Unique request identifier */
  private String requestId;

  /** Execution environment identifier */
  private int env;

  /** Request timeout in seconds */
  private Integer timeout;

  /**
   * Constructs a new Request with initialized context and parameters.
   */
  public Request() {
    this.context = new WeakHashMap<>();
    this.params = new RequestParams();
  }

  /**
   * Constructs a new Request with the specified request context.
   * Initializes context and parameters along with the provided request context.
   *
   * @param requestContext the request context to set
   */
  public Request(RequestContext requestContext) {
    this.context = new WeakHashMap<>();
    this.params = new RequestParams();
    this.requestContext = requestContext;
  }

  /**
   * Converts specified request fields to lowercase based on configuration.
   * Fields to be converted are defined in the SUNBIRD_API_REQUEST_LOWER_CASE_FIELDS config.
   */
  public void toLower() {
    Arrays.asList(
            ProjectUtil.getConfigValue(JsonKey.SUNBIRD_API_REQUEST_LOWER_CASE_FIELDS).split(","))
        .stream()
        .forEach(
            field -> {
              if (StringUtils.isNotBlank((String) this.getRequest().get(field))) {
                this.getRequest().put(field, ((String) this.getRequest().get(field)).toLowerCase());
              }
            });
  }

  /**
   * Gets the request identifier.
   *
   * @return the request ID
   */
  public String getRequestId() {
    return requestId;
  }

  /**
   * Gets the telemetry context.
   *
   * @return the context map
   */
  public Map<String, Object> getContext() {
    return context;
  }

  /**
   * Sets the telemetry context.
   *
   * @param context the context map to set
   */
  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  /**
   * Gets the request body data.
   *
   * @return the request map containing body data
   */
  public Map<String, Object> getRequest() {
    return request;
  }

  /**
   * Sets the request body data.
   *
   * @param request the request map to set
   */
  public void setRequest(Map<String, Object> request) {
    this.request = request;
  }

  /**
   * Gets a value from the request body by key.
   *
   * @param key the key to retrieve
   * @return the value associated with the key, or null if not found
   */
  public Object get(String key) {
    return request.get(key);
  }

  /**
   * Sets the request identifier.
   *
   * @param requestId the request ID to set
   */
  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  /**
   * Puts a key-value pair into the request body.
   *
   * @param key the key
   * @param vo the value
   */
  public void put(String key, Object vo) {
    request.put(key, vo);
  }

  /**
   * Gets the manager name handling this request.
   *
   * @return the manager name
   */
  public String getManagerName() {
    return managerName;
  }

  /**
   * Sets the manager name.
   *
   * @param managerName the manager name to set
   */
  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  /**
   * Gets the operation to be performed.
   *
   * @return the operation
   */
  public String getOperation() {
    return operation;
  }

  /**
   * Sets the operation to be performed.
   *
   * @param operation the operation to set
   */
  public void setOperation(String operation) {
    this.operation = operation;
  }

  @Override
  public String toString() {
    return "Request ["
        + (context != null ? "context=" + context + ", " : "")
        + (request != null ? "requestValueObjects=" + request : "")
        + "]";
  }

  /**
   * Gets the request identifier.
   *
   * @return the ID
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the request identifier.
   *
   * @param id the ID to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the request version.
   *
   * @return the version
   */
  public String getVer() {
    return ver;
  }

  /**
   * Sets the request version.
   *
   * @param ver the version to set
   */
  public void setVer(String ver) {
    this.ver = ver;
  }

  /**
   * Gets the request timestamp.
   *
   * @return the timestamp
   */
  public String getTs() {
    return ts;
  }

  /**
   * Sets the request timestamp.
   *
   * @param ts the timestamp to set
   */
  public void setTs(String ts) {
    this.ts = ts;
  }

  /**
   * Gets the request parameters.
   *
   * @return the request parameters
   */
  public RequestParams getParams() {
    return params;
  }

  /**
   * Sets the request parameters and updates message ID if needed.
   * If parameters lack a message ID and request ID is available, syncs them.
   *
   * @param params the request parameters to set
   */
  public void setParams(RequestParams params) {
    this.params = params;
    if (this.params.getMsgid() == null && requestId != null) {
      this.params.setMsgid(requestId);
    }
  }

  /**
   * Gets the execution environment identifier.
   *
   * @return the environment ID
   */
  public int getEnv() {
    return env;
  }

  /**
   * Sets the execution environment identifier.
   *
   * @param env the environment ID to set
   */
  public void setEnv(int env) {
    this.env = env;
  }

  /**
   * Gets the request timeout in seconds.
   * Returns the default timeout if not explicitly set.
   *
   * @return the timeout value in seconds
   */
  public Integer getTimeout() {
    return timeout == null ? WAIT_TIME_VALUE : timeout;
  }

  /**
   * Sets the request timeout in seconds.
   * Validates that the timeout is within acceptable range [0, 30].
   *
   * @param timeout the timeout value in seconds
   * @throws ProjectCommonException if timeout is outside valid range
   */
  public void setTimeout(Integer timeout) {
    if (timeout < MIN_TIMEOUT || timeout > MAX_TIMEOUT) {
      ProjectCommonException.throwServerErrorException(
          ResponseCode.invalidRequestTimeout,
          MessageFormat.format(ResponseCode.invalidRequestTimeout.getErrorMessage(), timeout));
    }
    this.timeout = timeout;
  }

  /**
   * Gets the request context.
   *
   * @return the request context
   */
  public RequestContext getRequestContext() {
    return requestContext;
  }

  /**
   * Sets the request context.
   *
   * @param requestContext the request context to set
   */
  public void setRequestContext(RequestContext requestContext) {
    this.requestContext = requestContext;
  }

  /**
   * Gets a value from the request body with a default fallback.
   *
   * @param key the key to retrieve
   * @param defaultVal the default value to return if key is not found
   * @return the value associated with the key, or the default value if not found
   */
  public Object getOrDefault(String key, Object defaultVal) {
    return request.getOrDefault(key, defaultVal);
  }

  /**
   * Checks if a key exists in the request body.
   *
   * @param key the key to check
   * @return true if the key exists in the request body, false otherwise
   */
  public Boolean contains(String key) {
    return request.containsKey(key);
  }
}

