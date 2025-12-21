package org.sunbird.response;

import java.io.Serializable;

/**
 * Encapsulates common response parameter data.
 * 
 * <p>This class serves as a simple data container for response metadata including
 * message identifiers, error information, and status tracking:
 * <ul>
 *   <li>Response message identifier (resmsgid)</li>
 *   <li>Request message identifier (msgid)</li>
 *   <li>Error code (err) and error message (errmsg)</li>
 *   <li>Response status (status)</li>
 * </ul>
 * 
 * <p>It is serializable for transmission and persistence.
 */
public class Params implements Serializable {

  private static final long serialVersionUID = -8786004970726124473L;

  /** Response message identifier */
  private String resmsgid;

  /** Request message identifier */
  private String msgid;

  /** Error code */
  private String err;

  /** Response status */
  private String status;

  /** Error message */
  private String errmsg;

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
   * Gets the request message identifier.
   *
   * @return the request message ID
   */
  public String getMsgid() {
    return msgid;
  }

  /**
   * Sets the request message identifier.
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
   * Gets the response status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the response status.
   *
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the error message.
   *
   * @return the error message
   */
  public String getErrmsg() {
    return errmsg;
  }

  /**
   * Sets the error message.
   *
   * @param errmsg the error message to set
   */
  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }
}

