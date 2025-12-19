package org.sunbird.request;

/**
 * Enumeration representing HTTP header parameters used throughout the application.
 * 
 * <p>This enum provides a centralized definition of all supported HTTP headers,
 * including both standard headers (e.g., content-type) and custom application headers
 * (e.g., x-consumer-id, x-authenticated-userid).
 * 
 * <p>Headers without explicit names use their enum constant name as the default,
 * while others have explicit lowercase representations for HTTP compatibility.
 */
public enum HeaderParam {
  REQUEST_ID,
  REQUEST_PATH,
  REQUEST_ST_ED_PATH,
  CURRENT_INVOCATION_PATH,
  USER_DATA,
  USER_LOCALE,
  SYSTEM_LOCALE,
  USER_ID,
  PROXY_USER_ID,
  USER_NAME,
  PROXY_USER_NAME,
  SCOPE_ID,
  X_Consumer_ID("x-consumer-id"),
  X_Session_ID("x-session-id"),
  X_Device_ID("x-device-id"),
  X_Authenticated_Userid("x-authenticated-userid"),
  ts("ts"),
  Content_Type("content-type"),
  X_Authenticated_User_Token("x-authenticated-user-token"),
  X_Authenticated_For("x-authenticated-for"),
  X_Authenticated_Client_Token("x-authenticated-client-token"),
  X_Authenticated_Client_Id("x-authenticated-client-id"),
  X_APP_ID("x-app-id"),
  CHANNEL_ID("x-channel-id"),
  X_Trace_ID("x-trace-id"),
  X_REQUEST_ID("x-request-id"),
  X_TRACE_ENABLED("x-trace-enabled"),
  X_APP_VERSION("x-app-ver"),
  X_APP_VERSION_PORTAL("x-app-version"),
  X_SOURCE("x-source"),
  X_Response_Length("x-response-length");

  /**
   * The HTTP header name corresponding to this enum constant.
   * Used for headers with custom lowercase representations.
   */
  private String name;

  /**
   * Constructor for HeaderParam enum constants with explicit header names.
   *
   * @param name the HTTP header name to use (typically lowercase)
   */
  private HeaderParam(String name) {
    this.name = name;
  }

  /**
   * Constructor for HeaderParam enum constants without explicit names.
   * Default behavior uses the enum constant name.
   */
  private HeaderParam() {
  }

  /**
   * Returns the parameter name based on the enum constant.
   * Uses the enum constant name in uppercase.
   *
   * @return the parameter name
   */
  public String getParamName() {
    return this.name();
  }

  /**
   * Returns the HTTP header name for this parameter.
   * For enum constants with explicit names, returns the specified name;
   * otherwise returns null.
   *
   * @return the HTTP header name, or null if not explicitly defined
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the HTTP header name for this parameter.
   *
   * @param name the HTTP header name to set
   */
  public void setName(String name) {
    this.name = name;
  }
}
