package org.sunbird.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.sunbird.exception.ResponseCode;

/**
 * Common response class used across all layers of the application.
 * 
 * <p>This class serves as the standard response envelope for all API responses,
 * containing metadata and result data. It provides:
 * <ul>
 *   <li>Response identification (id, version, timestamp)</li>
 *   <li>Response parameters and status information</li>
 *   <li>Response code for HTTP status mapping</li>
 *   <li>Flexible result data as key-value pairs</li>
 * </ul>
 * 
 * <p>The class implements {@link Serializable} and {@link Cloneable} for
 * transmission, persistence, and object cloning capabilities.
 */
public class Response implements Serializable, Cloneable {

  private static final long serialVersionUID = -3773253896160786443L;

  /** Response identifier */
  protected String id;

  /** API version */
  protected String ver;

  /** Response timestamp */
  protected String ts;

  /** Response parameters */
  protected ResponseParams params;

  /** Response code indicating status */
  protected ResponseCode responseCode = ResponseCode.OK;

  /** Result data as key-value pairs */
  protected Map<String, Object> result = new HashMap<>();

  /**
   * Gets the response identifier.
   *
   * @return the response ID
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the response identifier.
   *
   * @param id the response ID to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the API version.
   *
   * @return the API version
   */
  public String getVer() {
    return ver;
  }

  /**
   * Sets the API version.
   *
   * @param ver the API version to set
   */
  public void setVer(String ver) {
    this.ver = ver;
  }

  /**
   * Gets the response timestamp.
   *
   * @return the timestamp
   */
  public String getTs() {
    return ts;
  }

  /**
   * Sets the response timestamp.
   *
   * @param ts the timestamp to set
   */
  public void setTs(String ts) {
    this.ts = ts;
  }

  /**
   * Gets the result data map.
   *
   * @return the result map containing response data
   */
  public Map<String, Object> getResult() {
    return result;
  }

  /**
   * Gets a value from the result map by key.
   *
   * @param key the key to retrieve
   * @return the value associated with the key, or null if not found
   */
  public Object get(String key) {
    return result.get(key);
  }

  /**
   * Puts a key-value pair into the result map.
   *
   * @param key the key
   * @param vo the value
   */
  public void put(String key, Object vo) {
    result.put(key, vo);
  }

  /**
   * Puts all entries from the provided map into the result map.
   *
   * @param map the map to put
   */
  public void putAll(Map<String, Object> map) {
    result.putAll(map);
  }

  /**
   * Checks if a key exists in the result map.
   *
   * @param key the key to check
   * @return true if the key exists, false otherwise
   */
  public boolean containsKey(String key) {
    return result.containsKey(key);
  }

  /**
   * Gets the response parameters.
   *
   * @return the response parameters
   */
  public ResponseParams getParams() {
    return params;
  }

  /**
   * Sets the response parameters.
   *
   * @param params the response parameters to set
   */
  public void setParams(ResponseParams params) {
    this.params = params;
  }

  /**
   * Sets the response code.
   *
   * @param code the response code to set
   */
  public void setResponseCode(ResponseCode code) {
    this.responseCode = code;
  }

  /**
   * Gets the response code.
   *
   * @return the response code
   */
  public ResponseCode getResponseCode() {
    return this.responseCode;
  }

  /**
   * Creates a deep copy of the provided response.
   *
   * @param response the response to clone
   * @return a cloned copy of the response, or null if cloning fails
   */
  public Response clone(Response response) {
    try {
      return (Response) response.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }
}

