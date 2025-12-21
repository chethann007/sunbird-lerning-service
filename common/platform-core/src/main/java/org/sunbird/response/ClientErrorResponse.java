package org.sunbird.response;

import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;

/**
 * Represents an HTTP client error response.
 * 
 * <p>This class extends {@link Response} to handle client-side errors by wrapping
 * a {@link ProjectCommonException}. It automatically sets the response code to
 * {@link ResponseCode#CLIENT_ERROR} for consistency in error handling.
 * 
 * <p>Client errors typically include validation failures, malformed requests,
 * and other 4xx HTTP status scenarios.
 */
public class ClientErrorResponse extends Response {

  /** Exception details for the client error */
  private ProjectCommonException exception = null;

  /**
   * Constructs a new ClientErrorResponse with CLIENT_ERROR response code.
   */
  public ClientErrorResponse() {
    responseCode = ResponseCode.CLIENT_ERROR;
  }

  /**
   * Gets the exception associated with this error response.
   *
   * @return the project common exception
   */
  public ProjectCommonException getException() {
    return exception;
  }

  /**
   * Sets the exception for this error response.
   *
   * @param exception the project common exception to set
   */
  public void setException(ProjectCommonException exception) {
    this.exception = exception;
  }
}

