package org.sunbird.exception;

import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.keys.JsonKey;

/**
 * Common exception class for all application-level errors.
 * 
 * <p>This exception serves as the primary exception type across the entire backend,
 * providing structured error information including:
 * <ul>
 *   <li>Error code for client-side error identification and localization</li>
 *   <li>Localized error message for user consumption</li>
 *   <li>HTTP response code for appropriate HTTP status mapping</li>
 *   <li>ResponseCode enum for consistent error handling</li>
 * </ul>
 * 
 * <p>The exception supports both simple and parameterized message construction,
 * allowing flexible error message formatting with placeholder values.
 * 
 * <p>It provides static factory methods for creating and throwing specific error types:
 * client errors, resource not found, server errors, and unauthorized access errors.
 */
public class ProjectCommonException extends RuntimeException {

  /** Serial version UID for serialization */
  private static final long serialVersionUID = 1L;

  /** Error code for client-side error identification and localization */
  private String errorCode;

  /** Error message in English for client consumption */
  private String errorMessage;

  /** HTTP response code for the error */
  private int errorResponseCode;

  /** Response code enumeration */
  private ResponseCode responseCode;

  /**
   * Gets the error code used by clients to identify errors and perform message localization.
   *
   * @return the error code
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Sets the error code.
   *
   * @param code the error code to set
   */
  public void setErrorCode(String code) {
    this.errorCode = code;
  }

  /**
   * Gets the error message in English.
   *
   * @return the error message
   */
  @Override
  public String getMessage() {
    return errorMessage;
  }

  /**
   * Sets the error message.
   *
   * @param message the error message to set
   */
  public void setMessage(String message) {
    this.errorMessage = message;
  }

  /**
   * Gets the HTTP response code for this error.
   * Used in response headers for appropriate HTTP status mapping.
   *
   * @return the HTTP response code
   */
  public int getErrorResponseCode() {
    return errorResponseCode;
  }

  /**
   * Sets the HTTP response code.
   *
   * @param responseCode the HTTP response code to set
   */
  public void setErrorResponseCode(int responseCode) {
    this.errorResponseCode = responseCode;
  }

  /**
   * Gets the response code enumeration.
   *
   * @return the response code enum
   */
  public ResponseCode getResponseCode() {
    return responseCode;
  }

  /**
   * Sets the response code enumeration.
   *
   * @param responseCode the response code enum to set
   */
  public void setResponseCode(ResponseCode responseCode) {
    this.responseCode = responseCode;
  }

  /**
   * Gets the error message.
   *
   * @return the error message
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the error message.
   *
   * @param errorMessage the error message to set
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Constructs a ProjectCommonException with response code, message, and HTTP status code.
   *
   * @param code the response code enum
   * @param message the error message
   * @param responseCode the HTTP response code
   */
  public ProjectCommonException(ResponseCode code, String message, int responseCode) {
    super();
    this.responseCode = code;
    this.errorCode = code.getErrorCode();
    this.errorMessage = message;
    this.errorResponseCode = responseCode;
  }

  /**
   * Constructs a ProjectCommonException by copying from another exception
   * and adding operation context to the error code.
   *
   * @param pce the source ProjectCommonException
   * @param actorOperation the operation context to prepend to error code
   */
  public ProjectCommonException(ProjectCommonException pce, String actorOperation) {
    super();
    super.setStackTrace(pce.getStackTrace());
    this.errorCode =
        new StringBuilder(JsonKey.USER_ORG_SERVICE_PREFIX)
            .append(actorOperation)
            .append(pce.getErrorCode())
            .toString();
    this.errorResponseCode = pce.getErrorResponseCode();
    this.errorMessage = pce.getMessage();
    this.responseCode = pce.getResponseCode();
  }

  /**
   * Returns a string representation of this exception.
   *
   * @return the exception as a string in format "errorCode: errorMessage"
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(errorCode).append(": ");
    builder.append(errorMessage);
    return builder.toString();
  }

  /**
   * Constructs a ProjectCommonException with response code, parameterized message template,
   * HTTP response code, and placeholder values for message formatting.
   *
   * @param code the response code enum
   * @param messageWithPlaceholder the message template with placeholders
   * @param responseCode the HTTP response code
   * @param placeholderValue the values to substitute in the message template
   */
  public ProjectCommonException(
      ResponseCode code,
      String messageWithPlaceholder,
      int responseCode,
      String... placeholderValue) {
    super();
    this.errorCode = code.getErrorCode();
    this.errorMessage = MessageFormat.format(messageWithPlaceholder, (Object[]) placeholderValue);
    this.errorResponseCode = responseCode;
    this.responseCode = code;
  }

  // ==================== Static Factory Methods ====================

  /**
   * Throws a client error exception with the specified response code and message.
   *
   * @param responseCode the response code enum
   * @param exceptionMessage the error message (if blank, uses code's default message)
   * @throws ProjectCommonException always
   */
  public static void throwClientErrorException(ResponseCode responseCode, String exceptionMessage) {
    throw new ProjectCommonException(
        responseCode,
        StringUtils.isBlank(exceptionMessage) ? responseCode.getErrorMessage() : exceptionMessage,
        ResponseCode.CLIENT_ERROR.getResponseCode());
  }

  /**
   * Throws a resource not found exception with default message.
   *
   * @throws ProjectCommonException always
   */
  public static void throwResourceNotFoundException() {
    throw new ProjectCommonException(
        ResponseCode.resourceNotFound,
        MessageFormat.format(ResponseCode.resourceNotFound.getErrorMessage(), ""),
        ResponseCode.RESOURCE_NOT_FOUND.getResponseCode());
  }

  /**
   * Throws a resource not found exception with specified response code and message.
   *
   * @param responseCode the response code enum
   * @param exceptionMessage the error message (if blank, uses code's default message)
   * @throws ProjectCommonException always
   */
  public static void throwResourceNotFoundException(
      ResponseCode responseCode, String exceptionMessage) {
    throw new ProjectCommonException(
        responseCode,
        StringUtils.isBlank(exceptionMessage) ? responseCode.getErrorMessage() : exceptionMessage,
        ResponseCode.RESOURCE_NOT_FOUND.getResponseCode());
  }

  /**
   * Throws a server error exception with the specified response code and message.
   *
   * @param responseCode the response code enum
   * @param exceptionMessage the error message (if blank, uses code's default message)
   * @throws ProjectCommonException always
   */
  public static void throwServerErrorException(ResponseCode responseCode, String exceptionMessage) {
    throw new ProjectCommonException(
        responseCode,
        StringUtils.isBlank(exceptionMessage) ? responseCode.getErrorMessage() : exceptionMessage,
        ResponseCode.SERVER_ERROR.getResponseCode());
  }

  /**
   * Throws a server error exception with the specified response code and its default message.
   *
   * @param responseCode the response code enum
   * @throws ProjectCommonException always
   */
  public static void throwServerErrorException(ResponseCode responseCode) {
    throwServerErrorException(responseCode, responseCode.getErrorMessage());
  }

  /**
   * Throws a client error exception with the specified response code and its default message.
   *
   * @param responseCode the response code enum
   * @throws ProjectCommonException always
   */
  public static void throwClientErrorException(ResponseCode responseCode) {
    throwClientErrorException(responseCode, responseCode.getErrorMessage());
  }

  /**
   * Throws an unauthorized access error exception.
   *
   * @throws ProjectCommonException always with UNAUTHORIZED response code
   */
  public static void throwUnauthorizedErrorException() {
    throw new ProjectCommonException(
        ResponseCode.unAuthorized,
        ResponseCode.unAuthorized.getErrorMessage(),
        ResponseCode.UNAUTHORIZED.getResponseCode());
  }
}

