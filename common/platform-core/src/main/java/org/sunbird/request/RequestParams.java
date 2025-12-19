package org.sunbird.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

/**
 * Encapsulates request parameters and credentials required for API requests.
 * 
 * <p>This class manages both authentication and request identification parameters:
 * <ul>
 *   <li>Device and user identification (did, uid)</li>
 *   <li>Session and channel information (sid, cid)</li>
 *   <li>Request tracking (msgid, key)</li>
 *   <li>Authentication credentials (authToken)</li>
 * </ul>
 * 
 * <p>It is serializable for transmission over network and persistence.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestParams implements Serializable {

  private static final long serialVersionUID = -759588115950763188L;

  /** Device identifier */
  private String did;

  /** Request key */
  private String key;

  /** Message identifier */
  private String msgid;

  /** User identifier */
  private String uid;

  /** Channel identifier */
  private String cid;

  /** Session identifier */
  private String sid;

  /** Authentication token */
  private String authToken;

  /**
   * Gets the authentication token.
   *
   * @return the authentication token
   */
  public String getAuthToken() {
    return authToken;
  }

  /**
   * Sets the authentication token.
   *
   * @param authToken the authentication token to set
   */
  public void setAuthToken(String authToken) {
    this.authToken = authToken;
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
   * Gets the request key.
   *
   * @return the request key
   */
  public String getKey() {
    return key;
  }

  /**
   * Sets the request key.
   *
   * @param key the request key to set
   */
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Gets the message identifier.
   *
   * @return the message ID
   */
  public String getMsgid() {
    return msgid;
  }

  /**
   * Sets the message identifier.
   *
   * @param msgid the message ID to set
   */
  public void setMsgid(String msgid) {
    this.msgid = msgid;
  }

  /**
   * Gets the channel identifier.
   *
   * @return the channel ID
   */
  public String getCid() {
    return cid;
  }

  /**
   * Sets the channel identifier.
   *
   * @param cid the channel ID to set
   */
  public void setCid(String cid) {
    this.cid = cid;
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
}

