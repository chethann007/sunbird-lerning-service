package org.sunbird.response;

import java.io.Serializable;

/**
 * Encapsulates response parameter information for the response envelope.
 * 
 * <p>This class manages response metadata including message tracking, error information,
 * and status reporting:
 * <ul>
 *   <li>Response message identifier (resmsgid)</li>
 *   <li>Request message identifier (msgid)</li>
 *   <li>Error code (err) and error message (errmsg)</li>
 *   <li>API call status (status)</li>
 *   <li>Status type enumeration for categorization</li>
 * </ul>
 * 
 * <p>It is serializable for transmission and persistence.
 */
public class ResponseParams implements Serializable {

  private static final long serialVersionUID = 6772142067149203497L;

  /** Response message identifier */
  private String resmsgid;

  /** Request message identifier */
  private String msgid;

  /** Error code */
  private String err;

  /** API call status */
  private String status;

  /** Error message in English */
  private String errmsg;

  /**
   * Enumeration representing the status type of a response.
   */
  public enum StatusType {
    /** Successful response */
    SUCCESSFUL,
    /** Warning in response */
    WARNING,
    /** Failed response */
    FAILED;
  }

  /**
   * Gets the response message identifier.
   *
   * @return the response message ID
   */
  public String getResmsgid() {
    return resmsgid;
  }

  /**
   * Sets the response message identifier.
   *
   * @param resmsgid the response message ID to set
   */
  public void setResmsgid(String resmsgid) {
    this.resmsgid = resmsgid;
  }

  /**
   * Gets the request-specific message identifier.
   *
   * @return the request message ID
   */
  public String getMsgid() {
    return msgid;
  }

  /**
   * Sets the request-specific message identifier.
   *
   * @param msgid the request message ID to set
   */
  public void setMsgid(String msgid) {
    this.msgid = msgid;
  }

  /**
   * Gets the error code.
   *
   * @return the error code
   */
  public String getErr() {
    return err;
  }

  /**
   * Sets the error code.
   *
   * @param err the error code to set
   */
  public void setErr(String err) {
    this.err = err;
  }

  /**
   * Gets the API call status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the API call status.
   *
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the error message in English.
   *
   * @return the error message
   */
  public String getErrmsg() {
    return errmsg;
  }

  /**
   * Sets the error message in English.
   *
   * @param message the error message to set
   */
  public void setErrmsg(String message) {
    this.errmsg = message;
  }
}

