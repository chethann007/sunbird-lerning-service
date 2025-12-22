package org.sunbird.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates request context information including user, device, and session details.
 * 
 * <p>This class maintains both individual context attributes and aggregated context maps
 * for easier access and management of request-related metadata. It provides:
 * <ul>
 *   <li>User identification (uid) and device identification (did)</li>
 *   <li>Session information (sid) and source tracking</li>
 *   <li>Application metadata (appId, appVer)</li>
 *   <li>Request identification (reqId) and operation type (op)</li>
 *   <li>Debug configuration and telemetry context</li>
 * </ul>
 * 
 * <p>Both a parameterized and default constructor are provided for flexible initialization.
 */
@SuppressWarnings("unused")
public class RequestContext {
  /** User identifier */
  private String uid;

  /** Device identifier */
  private String did;

  /** Session identifier */
  private String sid;

  /** Application identifier */
  private String appId;

  /** Application version */
  private String appVer;

  /** Request identifier */
  private String reqId;

  /** Debug enabled flag */
  private String debugEnabled;

  /** Operation type */
  private String op;

  /** Request source */
  private String source;

  /** Context data as key-value pairs */
  private Map<String, Object> contextMap = new HashMap<>();

  /** Telemetry context data */
  private Map<String, Object> telemetryContext = new HashMap<>();

  /**
   * Constructs an empty RequestContext with initialized context and telemetry maps.
   */
  public RequestContext() {}

  /**
   * Constructs a RequestContext with specified values.
   * Initializes both individual attributes and the context map with provided values.
   *
   * @param uid the user identifier
   * @param did the device identifier
   * @param sid the session identifier
   * @param appId the application identifier
   * @param appVer the application version
   * @param reqId the request identifier
   * @param source the request source
   * @param debugEnabled the debug enabled flag
   * @param op the operation type
   */
  public RequestContext(
      String uid,
      String did,
      String sid,
      String appId,
      String appVer,
      String reqId,
      String source,
      String debugEnabled,
      String op) {
    super();
    this.uid = uid;
    this.did = did;
    this.sid = sid;
    this.appId = appId;
    this.appVer = appVer;
    this.reqId = reqId;
    this.source = source;
    this.debugEnabled = debugEnabled;
    this.op = op;

    contextMap.put("uid", uid);
    contextMap.put("did", did);
    contextMap.put("sid", sid);
    contextMap.put("appId", appId);
    contextMap.put("appVer", appVer);
    contextMap.put("reqId", reqId);
    contextMap.put("source", source);
    contextMap.put("op", op);
  }

  /**
   * Gets the user identifier.
   *
   * @return the user ID
   */
  public String getUid() {
    return uid;
  }

  /**
   * Sets the user identifier.
   *
   * @param uid the user ID to set
   */
  public void setUid(String uid) {
    this.uid = uid;
  }

  /**
   * Gets the device identifier.
   *
   * @return the device ID
   */
  public String getDid() {
    return did;
  }

  /**
   * Sets the device identifier.
   *
   * @param did the device ID to set
   */
  public void setDid(String did) {
    this.did = did;
  }

  /**
   * Gets the session identifier.
   *
   * @return the session ID
   */
  public String getSid() {
    return sid;
  }

  /**
   * Sets the session identifier.
   *
   * @param sid the session ID to set
   */
  public void setSid(String sid) {
    this.sid = sid;
  }

  /**
   * Gets the application identifier.
   *
   * @return the application ID
   */
  public String getAppId() {
    return appId;
  }

  /**
   * Sets the application identifier.
   *
   * @param appId the application ID to set
   */
  public void setAppId(String appId) {
    this.appId = appId;
  }

  /**
   * Gets the application version.
   *
   * @return the application version
   */
  public String getAppVer() {
    return appVer;
  }

  /**
   * Sets the application version.
   *
   * @param appVer the application version to set
   */
  public void setAppVer(String appVer) {
    this.appVer = appVer;
  }

  /**
   * Gets the request identifier.
   *
   * @return the request ID
   */
  public String getReqId() {
    return reqId;
  }

  /**
   * Sets the request identifier.
   *
   * @param reqId the request ID to set
   */
  public void setReqId(String reqId) {
    this.reqId = reqId;
  }

  /**
   * Gets the debug enabled flag.
   *
   * @return the debug enabled status
   */
  public String getDebugEnabled() {
    return debugEnabled;
  }

  /**
   * Sets the debug enabled flag.
   *
   * @param debugEnabled the debug enabled status to set
   */
  public void setDebugEnabled(String debugEnabled) {
    this.debugEnabled = debugEnabled;
  }

  /**
   * Gets the operation type.
   *
   * @return the operation type
   */
  public String getOp() {
    return op;
  }

  /**
   * Sets the operation type.
   *
   * @param op the operation type to set
   */
  public void setOp(String op) {
    this.op = op;
  }

  /**
   * Gets the context data map.
   *
   * @return the context map containing key-value pairs
   */
  public Map<String, Object> getContextMap() {
    return contextMap;
  }

  /**
   * Gets the telemetry context data.
   *
   * @return the telemetry context map
   */
  public Map<String, Object> getTelemetryContext() {
    return telemetryContext;
  }

  /**
   * Sets the telemetry context data.
   *
   * @param telemetryContext the telemetry context map to set
   */
  public void setTelemetryContext(Map<String, Object> telemetryContext) {
    this.telemetryContext = telemetryContext;
  }
}
