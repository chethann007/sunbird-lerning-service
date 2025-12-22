package org.sunbird.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of all response codes used throughout the application.
 * 
 * <p>This enum defines standardized response codes for both business logic errors
 * and HTTP status codes. Each code includes:
 * <ul>
 *   <li>Error code string for client-side identification</li>
 *   <li>Error message for user-facing communication</li>
 *   <li>HTTP response code for REST API status mapping</li>
 * </ul>
 * 
 * <p>Response codes are categorized into:
 * <ul>
 *   <li><strong>Business Logic Errors:</strong> Validation, data integrity, authorization failures</li>
 *   <li><strong>HTTP Status Codes:</strong> Standard HTTP responses (200, 400, 401, 403, 404, 429, 500, 503)</li>
 * </ul>
 * 
 * <p>The enum provides a reverse lookup mechanism to retrieve ResponseCode enums
 * by their HTTP response code value using {@link #getResponseCodeByCode(Integer)}.
 */
public enum ResponseCode {
  // ==================== Business Logic Error Codes ====================

  /** Unauthorized access */
  unAuthorized(ResponseMessage.Key.UNAUTHORIZED_USER, ResponseMessage.Message.UNAUTHORIZED_USER),

  /** Invalid operation name */
  invalidOperationName(
      ResponseMessage.Key.INVALID_OPERATION_NAME, ResponseMessage.Message.INVALID_OPERATION_NAME),

  /** Invalid request data */
  invalidRequestData(
      ResponseMessage.Key.INVALID_REQUESTED_DATA, ResponseMessage.Message.INVALID_REQUESTED_DATA),

  /** Success response */
  success(ResponseMessage.Key.SUCCESS_MESSAGE, ResponseMessage.Message.SUCCESS_MESSAGE),

  /** Duplicate entry error */
  errorDuplicateEntry(
      ResponseMessage.Key.ERROR_DUPLICATE_ENTRY, ResponseMessage.Message.ERROR_DUPLICATE_ENTRY),

  /** Parameter already exists */
  errorParamExists(
      ResponseMessage.Key.ERROR_PARAM_EXISTS, ResponseMessage.Message.ERROR_PARAM_EXISTS),

  /** Invalid OTP */
  errorInvalidOTP(ResponseMessage.Key.ERROR_INVALID_OTP, ResponseMessage.Message.ERROR_INVALID_OTP),

  /** Data type error */
  dataTypeError(ResponseMessage.Key.DATA_TYPE_ERROR, ResponseMessage.Message.DATA_TYPE_ERROR),

  /** Attribute conflict */
  errorAttributeConflict(
      ResponseMessage.Key.ERROR_ATTRIBUTE_CONFLICT,
      ResponseMessage.Message.ERROR_ATTRIBUTE_CONFLICT),

  /** Invalid property */
  invalidPropertyError(
      ResponseMessage.Key.INVALID_PROPERTY_ERROR, ResponseMessage.Message.INVALID_PROPERTY_ERROR),

  /** Data size exceeded */
  dataSizeError(ResponseMessage.Key.DATA_SIZE_EXCEEDED, ResponseMessage.Message.DATA_SIZE_EXCEEDED),

  /** User account locked */
  userAccountlocked(
      ResponseMessage.Key.USER_ACCOUNT_BLOCKED, ResponseMessage.Message.USER_ACCOUNT_BLOCKED),

  /** User status error */
  userStatusError(ResponseMessage.Key.USER_STATUS_MSG, ResponseMessage.Message.USER_STATUS_MSG),

  /** CSV file error */
  csvError(ResponseMessage.Key.INVALID_CSV_FILE, ResponseMessage.Message.INVALID_CSV_FILE),

  /** Invalid object type */
  invalidObjectType(
      ResponseMessage.Key.INVALID_OBJECT_TYPE, ResponseMessage.Message.INVALID_OBJECT_TYPE),

  /** CSV file is empty */
  csvFileEmpty(ResponseMessage.Key.EMPTY_CSV_FILE, ResponseMessage.Message.EMPTY_CSV_FILE),

  /** Data format error */
  dataFormatError(ResponseMessage.Key.DATA_FORMAT_ERROR, ResponseMessage.Message.DATA_FORMAT_ERROR),

  /** Only email or phone or managed by required */
  OnlyEmailorPhoneorManagedByRequired(
      ResponseMessage.Key.ONLY_EMAIL_OR_PHONE_OR_MANAGEDBY_REQUIRED,
      ResponseMessage.Message.ONLY_EMAIL_OR_PHONE_OR_MANAGEDBY_REQUIRED),

  /** Channel registration failed */
  channelRegFailed(
      ResponseMessage.Key.CHANNEL_REG_FAILED, ResponseMessage.Message.CHANNEL_REG_FAILED),

  /** Resource not found */
  resourceNotFound(
      ResponseMessage.Key.RESOURCE_NOT_FOUND, ResponseMessage.Message.RESOURCE_NOT_FOUND),

  /** Size limit exceeded */
  sizeLimitExceed(
      ResponseMessage.Key.MAX_ALLOWED_SIZE_LIMIT_EXCEED,
      ResponseMessage.Message.MAX_ALLOWED_SIZE_LIMIT_EXCEED),

  /** Inactive user */
  inactiveUser(ResponseMessage.Key.INACTIVE_USER, ResponseMessage.Message.INACTIVE_USER),

  /** Invalid value */
  invalidValue(ResponseMessage.Key.INVALID_VALUE, ResponseMessage.Message.INVALID_VALUE),

  /** Invalid parameter */
  invalidParameter(
      ResponseMessage.Key.INVALID_PARAMETER, ResponseMessage.Message.INVALID_PARAMETER),

  /** Invalid location delete request */
  invalidLocationDeleteRequest(
      ResponseMessage.Key.INVALID_LOCATION_DELETE_REQUEST,
      ResponseMessage.Message.INVALID_LOCATION_DELETE_REQUEST),

  /** Mandatory parameters missing */
  mandatoryParamsMissing(
      ResponseMessage.Key.MANDATORY_PARAMETER_MISSING,
      ResponseMessage.Message.MANDATORY_PARAMETER_MISSING),

  /** Mandatory parameters empty */
  errorMandatoryParamsEmpty(
      ResponseMessage.Key.ERROR_MANDATORY_PARAMETER_EMPTY,
      ResponseMessage.Message.ERROR_MANDATORY_PARAMETER_EMPTY),

  /** No framework found */
  errorNoFrameworkFound(
      ResponseMessage.Key.ERROR_NO_FRAMEWORK_FOUND,
      ResponseMessage.Message.ERROR_NO_FRAMEWORK_FOUND),

  /** Field cannot be updated */
  unupdatableField(
      ResponseMessage.Key.UPDATE_NOT_ALLOWED, ResponseMessage.Message.UPDATE_NOT_ALLOWED),

  /** Invalid parameter value */
  invalidParameterValue(
      ResponseMessage.Key.INVALID_PARAMETER_VALUE, ResponseMessage.Message.INVALID_PARAMETER_VALUE),

  /** Parent not allowed */
  parentNotAllowed(
      ResponseMessage.Key.PARENT_NOT_ALLOWED, ResponseMessage.Message.PARENT_NOT_ALLOWED),

  /** Missing file attachment */
  missingFileAttachment(
      ResponseMessage.Key.MISSING_FILE_ATTACHMENT, ResponseMessage.Message.MISSING_FILE_ATTACHMENT),

  /** File attachment size not configured */
  fileAttachmentSizeNotConfigured(
      ResponseMessage.Key.FILE_ATTACHMENT_SIZE_NOT_CONFIGURED,
      ResponseMessage.Message.FILE_ATTACHMENT_SIZE_NOT_CONFIGURED),

  /** Empty file */
  emptyFile(ResponseMessage.Key.EMPTY_FILE, ResponseMessage.Message.EMPTY_FILE),

  /** Invalid columns */
  invalidColumns(ResponseMessage.Key.INVALID_COLUMNS, ResponseMessage.Message.INVALID_COLUMNS),

  /** Conflicting org locations */
  conflictingOrgLocations(
      ResponseMessage.Key.CONFLICTING_ORG_LOCATIONS,
      ResponseMessage.Message.CONFLICTING_ORG_LOCATIONS),

  /** Empty header line */
  emptyHeaderLine(ResponseMessage.Key.EMPTY_HEADER_LINE, ResponseMessage.Message.EMPTY_HEADER_LINE),

  /** Invalid request parameter */
  invalidRequestParameter(
      ResponseMessage.Key.INVALID_REQUEST_PARAMETER,
      ResponseMessage.Message.INVALID_REQUEST_PARAMETER),

  /** Root org association error */
  rootOrgAssociationError(
      ResponseMessage.Key.ROOT_ORG_ASSOCIATION_ERROR,
      ResponseMessage.Message.ROOT_ORG_ASSOCIATION_ERROR),

  /** Dependent parameter missing */
  dependentParameterMissing(
      ResponseMessage.Key.DEPENDENT_PARAMETER_MISSING,
      ResponseMessage.Message.DEPENDENT_PARAMETER_MISSING),

  /** External ID assigned to other user */
  externalIdAssignedToOtherUser(
      ResponseMessage.Key.EXTERNALID_ASSIGNED_TO_OTHER_USER,
      ResponseMessage.Message.EXTERNALID_ASSIGNED_TO_OTHER_USER),

  /** Duplicate external IDs */
  duplicateExternalIds(
      ResponseMessage.Key.DUPLICATE_EXTERNAL_IDS, ResponseMessage.Message.DUPLICATE_EXTERNAL_IDS),

  /** Email recipients exceeded max limit */
  emailNotSentRecipientsExceededMaxLimit(
      ResponseMessage.Key.EMAIL_RECIPIENTS_EXCEEDS_MAX_LIMIT,
      ResponseMessage.Message.EMAIL_RECIPIENTS_EXCEEDS_MAX_LIMIT),

  /** Parameter mismatch */
  parameterMismatch(
      ResponseMessage.Key.PARAMETER_MISMATCH, ResponseMessage.Message.PARAMETER_MISMATCH),

  /** Forbidden */
  errorForbidden(ResponseMessage.Key.FORBIDDEN, ResponseMessage.Message.FORBIDDEN),

  /** Error config load empty string */
  errorConfigLoadEmptyString(
      ResponseMessage.Key.ERROR_CONFIG_LOAD_EMPTY_STRING,
      ResponseMessage.Message.ERROR_CONFIG_LOAD_EMPTY_STRING),

  /** Error config load parse string */
  errorConfigLoadParseString(
      ResponseMessage.Key.ERROR_CONFIG_LOAD_PARSE_STRING,
      ResponseMessage.Message.ERROR_CONFIG_LOAD_PARSE_STRING),

  /** Error config load empty config */
  errorConfigLoadEmptyConfig(
      ResponseMessage.Key.ERROR_CONFIG_LOAD_EMPTY_CONFIG,
      ResponseMessage.Message.ERROR_CONFIG_LOAD_EMPTY_CONFIG),

  /** Error no root org associated */
  errorNoRootOrgAssociated(
      ResponseMessage.Key.ERROR_NO_ROOT_ORG_ASSOCIATED,
      ResponseMessage.Message.ERROR_NO_ROOT_ORG_ASSOCIATED),

  /** Error unsupported cloud storage */
  errorUnsupportedCloudStorage(
      ResponseMessage.Key.ERROR_UNSUPPORTED_CLOUD_STORAGE,
      ResponseMessage.Message.ERROR_UNSUPPORTED_CLOUD_STORAGE),

  /** Error unsupported field */
  errorUnsupportedField(
      ResponseMessage.Key.ERROR_UNSUPPORTED_FIELD, ResponseMessage.Message.ERROR_UNSUPPORTED_FIELD),

  /** Error inactive org */
  errorInactiveOrg(
      ResponseMessage.Key.ERROR_INACTIVE_ORG, ResponseMessage.Message.ERROR_INACTIVE_ORG),

  /** Error duplicate entries */
  errorDuplicateEntries(
      ResponseMessage.Key.ERROR_DUPLICATE_ENTRIES, ResponseMessage.Message.ERROR_DUPLICATE_ENTRIES),

  /** Error conflicting values */
  errorConflictingValues(
      ResponseMessage.Key.ERROR_CONFLICTING_VALUES,
      ResponseMessage.Message.ERROR_CONFLICTING_VALUES),

  /** Error conflicting root org ID */
  errorConflictingRootOrgId(
      ResponseMessage.Key.ERROR_CONFLICTING_ROOT_ORG_ID,
      ResponseMessage.Message.ERROR_CONFLICTING_ROOT_ORG_ID),

  /** Error invalid parameter size */
  errorInvalidParameterSize(
      ResponseMessage.Key.ERROR_INVALID_PARAMETER_SIZE,
      ResponseMessage.Message.ERROR_INVALID_PARAMETER_SIZE),

  /** Error rate limit exceeded */
  errorRateLimitExceeded(
      ResponseMessage.Key.ERROR_RATE_LIMIT_EXCEEDED,
      ResponseMessage.Message.ERROR_RATE_LIMIT_EXCEEDED),

  /** Invalid request timeout */
  invalidRequestTimeout(
      ResponseMessage.Key.INVALID_REQUEST_TIMEOUT, ResponseMessage.Message.INVALID_REQUEST_TIMEOUT),

  /** Error user migration failed */
  errorUserMigrationFailed(
      ResponseMessage.Key.ERROR_USER_MIGRATION_FAILED,
      ResponseMessage.Message.ERROR_USER_MIGRATION_FAILED),

  /** Invalid identifier */
  invalidIdentifier(
      ResponseMessage.Key.VALID_IDENTIFIER_ABSENSE,
      ResponseMessage.Message.IDENTIFIER_VALIDATION_FAILED),

  /** Mandatory header parameters missing */
  mandatoryHeaderParamsMissing(
      ResponseMessage.Key.MANDATORY_HEADER_PARAMETER_MISSING,
      ResponseMessage.Message.MANDATORY_HEADER_PARAMETER_MISSING),

  /** Recovery params match exception */
  recoveryParamsMatchException(
      ResponseMessage.Key.RECOVERY_PARAM_MATCH_EXCEPTION,
      ResponseMessage.Message.RECOVERY_PARAM_MATCH_EXCEPTION),

  /** Password validation failed */
  passwordValidation(
      ResponseMessage.Key.INVALID_PASSWORD, ResponseMessage.Message.INVALID_PASSWORD),

  /** OTP verification failed */
  otpVerificationFailed(
      ResponseMessage.Key.OTP_VERIFICATION_FAILED, ResponseMessage.Message.OTP_VERIFICATION_FAILED),

  /** Service unavailable */
  serviceUnAvailable(
      ResponseMessage.Key.SERVICE_UNAVAILABLE, ResponseMessage.Message.SERVICE_UNAVAILABLE),

  /** Managed by not allowed */
  managedByNotAllowed(
      ResponseMessage.Key.MANAGED_BY_NOT_ALLOWED, ResponseMessage.Message.MANAGED_BY_NOT_ALLOWED),

  /** Managed user limit exceeded */
  managedUserLimitExceeded(
      ResponseMessage.Key.MANAGED_USER_LIMIT_EXCEEDED,
      ResponseMessage.Message.MANAGED_USER_LIMIT_EXCEEDED),

  /** Invalid captcha */
  invalidCaptcha(ResponseMessage.Key.INVALID_CAPTCHA, ResponseMessage.Message.INVALID_CAPTCHA),

  /** Declared user error status not updated */
  declaredUserErrorStatusNotUpdated(
      ResponseMessage.Key.DECLARED_USER_ERROR_STATUS_IS_NOT_UPDATED,
      ResponseMessage.Message.DECLARED_USER_ERROR_STATUS_IS_NOT_UPDATED),

  /** Declared user validated status not updated */
  declaredUserValidatedStatusNotUpdated(
      ResponseMessage.Key.DECLARED_USER_VALIDATED_STATUS_IS_NOT_UPDATED,
      ResponseMessage.Message.DECLARED_USER_VALIDATED_STATUS_IS_NOT_UPDATED),

  /** Invalid consent status */
  invalidConsentStatus(
      ResponseMessage.Key.INVALID_CONSENT_STATUS, ResponseMessage.Message.INVALID_CONSENT_STATUS),

  /** Server error */
  serverError(ResponseMessage.Key.SERVER_ERROR, ResponseMessage.Message.SERVER_ERROR),

  /** Invalid file extension */
  invalidFileExtension(
      ResponseMessage.Key.INVALID_FILE_EXTENSION, ResponseMessage.Message.INVALID_FILE_EXTENSION),

  /** Invalid encryption file */
  invalidEncryptionFile(
      ResponseMessage.Key.INVALID_ENCRYPTION_FILE, ResponseMessage.Message.INVALID_ENCRYPTION_FILE),

  /** Invalid security level */
  invalidSecurityLevel(
      ResponseMessage.Key.INVALID_SECURITY_LEVEL, ResponseMessage.Message.INVALID_SECURITY_LEVEL),

  /** Invalid security level lower */
  invalidSecurityLevelLower(
      ResponseMessage.Key.INVALID_SECURITY_LEVEL_LOWER,
      ResponseMessage.Message.INVALID_SECURITY_LEVEL_LOWER),

  /** Default security level config missing */
  defaultSecurityLevelConfigMissing(
      ResponseMessage.Key.MISSING_DEFAULT_SECURITY_LEVEL,
      ResponseMessage.Message.MISSING_DEFAULT_SECURITY_LEVEL),

  /** Invalid tenant security level lower */
  invalidTenantSecurityLevelLower(
      ResponseMessage.Key.INVALID_TENANT_SECURITY_LEVEL_LOWER,
      ResponseMessage.Message.INVALID_TENANT_SECURITY_LEVEL_LOWER),

  /** Cannot delete user */
  cannotDeleteUser(
      ResponseMessage.Key.CANNOT_DELETE_USER, ResponseMessage.Message.CANNOT_DELETE_USER),

  /** Cannot transfer ownership */
  cannotTransferOwnership(
      ResponseMessage.Key.CANNOT_TRANSFER_OWNERSHIP, ResponseMessage.Message.CANNOT_TRANSFER_OWNERSHIP),

  // ==================== HTTP Status Codes ====================

  /** HTTP 200 OK */
  OK(200),

  /** HTTP 200 Success */
  SUCCESS(200),

  /** HTTP 400 Bad Request */
  CLIENT_ERROR(400),

  /** HTTP 500 Internal Server Error */
  SERVER_ERROR(500),

  /** HTTP 404 Not Found */
  RESOURCE_NOT_FOUND(404),

  /** HTTP 401 Unauthorized */
  UNAUTHORIZED(401),

  /** HTTP 403 Forbidden */
  FORBIDDEN(403),

  /** HTTP 302 Redirect */
  REDIRECTION_REQUIRED(302),

  /** HTTP 429 Too Many Requests */
  TOO_MANY_REQUESTS(429),

  /** HTTP 503 Service Unavailable */
  SERVICE_UNAVAILABLE(503),

  /** HTTP 206 Partial Content */
  PARTIAL_SUCCESS_RESPONSE(206),

  /** HTTP 418 I'm a Teapot */
  IM_A_TEAPOT(418),

  /** Extended user profile not loaded */
  extendUserProfileNotLoaded(
      ResponseMessage.Key.EXTENDED_USER_PROFILE_NOT_LOADED,
      ResponseMessage.Message.EXTENDED_USER_PROFILE_NOT_LOADED),

  /** Role processing invalid org error */
  roleProcessingInvalidOrgError(
      ResponseMessage.Key.ROLE_PROCESSING_INVALID_ORG,
      ResponseMessage.Message.ROLE_PROCESSING_INVALID_ORG);

  // ==================== Fields ====================

  /** HTTP response code value */
  private int responseCode;

  /** Error code string for client identification */
  private String errorCode;

  /** Error message for user communication */
  private String errorMessage;

  // ==================== Constructors ====================

  /**
   * Constructs a ResponseCode with error code and message.
   *
   * @param errorCode the error code string
   * @param errorMessage the error message
   */
  ResponseCode(String errorCode, String errorMessage) {
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * Constructs a ResponseCode with HTTP response code.
   *
   * @param responseCode the HTTP response code
   */
  ResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  // ==================== Getters and Setters ====================

  /**
   * Gets the error code.
   *
   * @return the error code string
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Sets the error code.
   *
   * @param errorCode the error code to set
   */
  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
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
   * Gets the HTTP response code.
   *
   * @return the HTTP response code
   */
  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Sets the HTTP response code.
   *
   * @param responseCode the HTTP response code to set
   */
  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  // ==================== Static Lookup ====================

  /** Reverse mapping of response codes by HTTP code */
  private static final Map<Integer, ResponseCode> responseCodeByCode = new HashMap<>();

  static {
    responseCodeByCode.put(200, ResponseCode.OK);
    responseCodeByCode.put(400, ResponseCode.CLIENT_ERROR);
    responseCodeByCode.put(500, ResponseCode.SERVER_ERROR);
    responseCodeByCode.put(404, ResponseCode.RESOURCE_NOT_FOUND);
    responseCodeByCode.put(401, ResponseCode.UNAUTHORIZED);
    responseCodeByCode.put(403, ResponseCode.FORBIDDEN);
    responseCodeByCode.put(302, ResponseCode.REDIRECTION_REQUIRED);
    responseCodeByCode.put(429, ResponseCode.TOO_MANY_REQUESTS);
    responseCodeByCode.put(503, ResponseCode.SERVICE_UNAVAILABLE);
    responseCodeByCode.put(206, ResponseCode.PARTIAL_SUCCESS_RESPONSE);
    responseCodeByCode.put(418, ResponseCode.IM_A_TEAPOT);
  }

  /**
   * Retrieves a ResponseCode by its HTTP response code value.
   * Returns {@link #OK} if no matching code is found.
   *
   * @param code the HTTP response code
   * @return the corresponding ResponseCode, or OK if not found
   */
  public static ResponseCode getResponseCodeByCode(Integer code) {
    ResponseCode responseCode = responseCodeByCode.get(code);
    if (null != responseCode) {
      return responseCode;
    } else {
      return ResponseCode.OK;
    }
  }
}

