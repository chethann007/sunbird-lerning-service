package org.sunbird.exception;

import org.sunbird.keys.JsonKey;

/**
 * Container interface for all standardized response messages used throughout the application.
 * 
 * <p>This interface provides two nested interfaces:
 * <ul>
 *   <li>{@link Message} - User-facing error and status messages with format placeholders</li>
 *   <li>{@link Key} - Unique message identifiers for localization and client-side mapping</li>
 * </ul>
 * 
 * <p>Messages support parameterized formatting using MessageFormat syntax with {0}, {1}, etc.
 * placeholders for dynamic values. Each message key has a corresponding message template
 * that provides appropriate user-facing content for the identified error condition.
 * 
 * <p>The separation of keys and messages allows for:
 * <ul>
 *   <li>Multi-language support through key-based localization</li>
 *   <li>Consistent error identification across clients and services</li>
 *   <li>Template-based message formatting with dynamic parameters</li>
 * </ul>
 */
public interface ResponseMessage {

  /**
   * Nested interface containing all user-facing error and status message templates.
   * 
   * <p>Messages may contain placeholders ({0}, {1}, etc.) for dynamic value insertion
   * using MessageFormat. Examples:
   * <ul>
   *   <li>ERROR_DUPLICATE_ENTRY: "Value {0} for {1} is already in use."</li>
   *   <li>DATA_TYPE_ERROR: "Data type of {0} should be {1}."</li>
   *   <li>ERROR_ATTRIBUTE_CONFLICT: "Either pass attribute {0} or {1} but not both."</li>
   * </ul>
   */
  interface Message {

    // ==================== Authentication & Authorization ====================

    /** Unauthorized access message */
    String UNAUTHORIZED_USER = "You are not authorized.";

    /** Invalid operation name message */
    String INVALID_OPERATION_NAME =
        "Operation name is invalid. Please provide a valid operation name";

    /** Invalid request data message */
    String INVALID_REQUESTED_DATA = "Requested data for this operation is not valid.";

    /** Success message */
    String SUCCESS_MESSAGE = "Success";

    // ==================== Data Validation & Integrity ====================

    /** Duplicate entry error message */
    String ERROR_DUPLICATE_ENTRY = "Value {0} for {1} is already in use.";

    /** Parameter already exists message */
    String ERROR_PARAM_EXISTS = "{0} already exists";

    /** Invalid OTP message */
    String ERROR_INVALID_OTP = "Invalid OTP.";

    /** Data type error message */
    String DATA_TYPE_ERROR = "Data type of {0} should be {1}.";

    /** Attribute conflict message */
    String ERROR_ATTRIBUTE_CONFLICT = "Either pass attribute {0} or {1} but not both.";

    /** Invalid property error message */
    String INVALID_PROPERTY_ERROR = "Invalid property {0}.";

    /** Data size exceeded message */
    String DATA_SIZE_EXCEEDED = "Maximum upload data size should be {0}";

    /** Data format error message */
    String DATA_FORMAT_ERROR = "Invalid format for given {0}.";

    /** Duplicate entries message */
    String ERROR_DUPLICATE_ENTRIES = "System contains duplicate entry for {0}.";

    /** Conflicting values error message */
    String ERROR_CONFLICTING_VALUES = "Conflicting values for {0} ({1}) and {2} ({3}).";

    /** Duplicate external IDs message */
    String DUPLICATE_EXTERNAL_IDS =
        "Duplicate external IDs for given idType ({0}) and provider ({1}).";

    // ==================== User & Account Management ====================

    /** User account locked message */
    String USER_ACCOUNT_BLOCKED = "User account has been blocked .";

    /** User status error message */
    String USER_STATUS_MSG = "User is already {0}.";

    /** Inactive user message */
    String INACTIVE_USER = "User is Inactive. Please make it active to proceed.";

    /** Cannot delete user message */
    String CANNOT_DELETE_USER = "User is restricted from deleting account based on roles!";

    /** Cannot transfer ownership message */
    String CANNOT_TRANSFER_OWNERSHIP = "User is restricted from transfering the ownership based on roles!";

    // ==================== CSV & File Upload ====================

    /** Invalid CSV file message */
    String INVALID_CSV_FILE = "Please provide valid csv file.";

    /** CSV file is empty message */
    String EMPTY_CSV_FILE = "CSV file is Empty.";

    /** Empty header line message */
    String EMPTY_HEADER_LINE = "Missing header line in CSV file.";

    /** Invalid columns message */
    String INVALID_COLUMNS = "Invalid column: {0}. Valid columns are: {1}.";

    /** Missing file attachment message */
    String MISSING_FILE_ATTACHMENT = "Missing file attachment.";

    /** Empty file message */
    String EMPTY_FILE = "Attached file is empty.";

    /** File attachment size not configured message */
    String FILE_ATTACHMENT_SIZE_NOT_CONFIGURED = "File attachment max size is not configured.";

    /** Invalid file extension message */
    String INVALID_FILE_EXTENSION = "Please provide a valid file. File expected of format: {0}";

    // ==================== Organization & Channel ====================

    /** Channel registration failed message */
    String CHANNEL_REG_FAILED = "Channel Registration failed.";

    /** Resource not found message */
    String RESOURCE_NOT_FOUND = "Requested {0} resource not found";

    /** Conflicting org locations message */
    String CONFLICTING_ORG_LOCATIONS =
        "An organisation cannot be associated to two conflicting locations ({0}, {1}) at {2} level. ";

    /** Invalid location delete request message */
    String INVALID_LOCATION_DELETE_REQUEST =
        "One or more locations have a parent reference to given location and hence cannot be deleted.";

    /** Parent not allowed message */
    String PARENT_NOT_ALLOWED = "For top level location, {0} is not allowed.";

    /** Root org association error message */
    String ROOT_ORG_ASSOCIATION_ERROR =
        "No root organisation found which is associated with given {0}.";

    /** Error no root org associated message */
    String ERROR_NO_ROOT_ORG_ASSOCIATED = "Not able to associate with root org";

    /** Error inactive org message */
    String ERROR_INACTIVE_ORG = "Organisation corresponding to given {0} ({1}) is inactive.";

    /** Error conflicting root org ID message */
    String ERROR_CONFLICTING_ROOT_ORG_ID =
        "Root organisation channel of uploader user is conflicting with that of specified organisation ID/orgExternalId channel value.";

    // ==================== Parameter & Value Validation ====================

    /** Mandatory parameter missing message */
    String MANDATORY_PARAMETER_MISSING = "Mandatory parameter {0} is missing.";

    /** Mandatory parameter empty message */
    String ERROR_MANDATORY_PARAMETER_EMPTY = "Mandatory parameter {0} is empty.";

    /** Mandatory header parameter missing message */
    String MANDATORY_HEADER_PARAMETER_MISSING = "Mandatory header parameter {0} is missing.";

    /** Invalid parameter message */
    String INVALID_PARAMETER = "Please provide valid {0}.";

    /** Invalid parameter value message */
    String INVALID_PARAMETER_VALUE =
        "Invalid value {0} for parameter {1}. Please provide a valid value.";

    /** Invalid value message */
    String INVALID_VALUE = "Invalid {0}: {1}. Valid values are: {2}.";

    /** Invalid request parameter message */
    String INVALID_REQUEST_PARAMETER = "Invalid parameter {0} in request.";

    /** Parameter mismatch message */
    String PARAMETER_MISMATCH = "Mismatch of given parameters: {0}.";

    /** Dependent parameter missing message */
    String DEPENDENT_PARAMETER_MISSING = "Missing parameter {0} which is dependent on {1}.";

    /** Invalid parameter size message */
    String ERROR_INVALID_PARAMETER_SIZE =
        "Parameter {0} is of invalid size (expected: {1}, actual: {2}).";

    // ==================== Error & Exception Handling ====================

    /** Invalid object type message */
    String INVALID_OBJECT_TYPE = "Invalid Object Type.";

    /** Error no framework found message */
    String ERROR_NO_FRAMEWORK_FOUND = "No framework found.";

    /** Update not allowed message */
    String UPDATE_NOT_ALLOWED = "Update of {0} is not allowed.";

    /** Unupdatable field message */
    String UNUPDATABLE_FIELD = "Field {0} cannot be updated.";

    /** Recovery params match exception message */
    String RECOVERY_PARAM_MATCH_EXCEPTION = "{0} could not be same as {1}";

    // ==================== Email & Notification ====================

    /** Only email or phone or managed by required message */
    String ONLY_EMAIL_OR_PHONE_OR_MANAGEDBY_REQUIRED =
        "Please provide only email or phone or managed by";

    /** Email recipients exceeded max limit message */
    String EMAIL_RECIPIENTS_EXCEEDS_MAX_LIMIT =
        "Email notification is not sent as the number of recipients exceeded configured limit ({0}).";

    // ==================== Size & Limits ====================

    /** Maximum upload data size exceeded message */
    String MAX_ALLOWED_SIZE_LIMIT_EXCEED = "Max allowed size is {0}";

    /** Rate limit exceeded message */
    String ERROR_RATE_LIMIT_EXCEEDED =
        "Your per {0} rate limit has exceeded. You can retry after some time.";

    /** Invalid request timeout message */
    String INVALID_REQUEST_TIMEOUT = "Invalid request timeout value {0}.";

    /** Managed user limit exceeded message */
    String MANAGED_USER_LIMIT_EXCEEDED = "Managed user creation limit exceeded";

    // ==================== External ID & Identity ====================

    /** External ID assigned to other user message */
    String EXTERNALID_ASSIGNED_TO_OTHER_USER =
        "External ID (id: {0}, idType: {1}, provider: {2}) already assigned to another user.";

    /** External ID not found message */
    String EXTERNALID_NOT_FOUND =
        "External ID (id: {0}, idType: {1}, provider: {2}) not found for given user.";

    /** External ID format message */
    String EXTERNAL_ID_FORMAT = "externalId (id: {0}, idType: {1}, provider: {2})";

    /** Identifier validation failed message */
    String IDENTIFIER_VALIDATION_FAILED =
        "Valid identifier is not present in List, Valid supported identifiers are ";

    /** Valid identifier absence message */
    String VALID_IDENTIFIER_ABSENSE = "Valid identifier is absent";

    // ==================== Security & Encryption ====================

    /** Invalid password message */
    String INVALID_PASSWORD =
        "Password must contain a minimum of 8 characters including numerals, lower and upper case alphabets and special characters";

    /** Invalid OTP message (verification) */
    String OTP_VERIFICATION_FAILED = "OTP verification failed. Remaining attempt count is {0}.";

    /** Invalid captcha message */
    String INVALID_CAPTCHA = "Captcha is invalid";

    /** Invalid encryption file message */
    String INVALID_ENCRYPTION_FILE = "Please provide valid public key file.";

    /** Invalid security level message */
    String INVALID_SECURITY_LEVEL =
        "Invalid data security level {0} provided for job {1}. Please provide a valid data security level.";

    /** Invalid security level lower message */
    String INVALID_SECURITY_LEVEL_LOWER =
        "Invalid data security level {0} provided for job {1}. Cannot be set lower than the default security level: {2}";

    /** Missing default security level message */
    String MISSING_DEFAULT_SECURITY_LEVEL =
        "Default data security policy settings is missing for the job: {0}";

    /** Invalid tenant security level lower message */
    String INVALID_TENANT_SECURITY_LEVEL_LOWER =
        "Tenant level's security {0} cannot be lower than system level's security {1}. Please provide a valid data security level.";

    // ==================== Configuration & System ====================

    /** Error config load empty string message */
    String ERROR_CONFIG_LOAD_EMPTY_STRING =
        "Loading {0} configuration failed as empty string is passed as parameter.";

    /** Error config load parse string message */
    String ERROR_CONFIG_LOAD_PARSE_STRING =
        "Loading {0} configuration failed due to parsing error.";

    /** Error config load empty config message */
    String ERROR_CONFIG_LOAD_EMPTY_CONFIG = "Loading {0} configuration failed.";

    /** Error unsupported cloud storage message */
    String ERROR_UNSUPPORTED_CLOUD_STORAGE = "Unsupported cloud storage type {0}.";

    /** Error unsupported field message */
    String ERROR_UNSUPPORTED_FIELD = "Unsupported field {0}.";

    /** Service unavailable message */
    String SERVICE_UNAVAILABLE = "SERVICE UNAVAILABLE";

    /** Server error message */
    String SERVER_ERROR = "server error";

    // ==================== User Management & Consent ====================

    /** Managed by not allowed message */
    String MANAGED_BY_NOT_ALLOWED = "managedBy cannot be updated.";

    /** Invalid consent status message */
    String INVALID_CONSENT_STATUS = "Consent status is invalid";

    /** Declared user error status not updated message */
    String DECLARED_USER_ERROR_STATUS_IS_NOT_UPDATED = "Declared user error status is not updated";

    /** Declared user validated status not updated message */
    String DECLARED_USER_VALIDATED_STATUS_IS_NOT_UPDATED =
        "Declared user validated status is not updated";

    /** Extended user profile not loaded message */
    String EXTENDED_USER_PROFILE_NOT_LOADED =
        "Failed to load extendedProfileSchemaConfig from System_Settings table";

    /** User type config is empty message */
    String USER_TYPE_CONFIG_IS_EMPTY = "userType config is empty for the statecode {0}";

    /** Role processing invalid org message */
    String ROLE_PROCESSING_INVALID_ORG =
        "Error while processing assign role. Invalid Organisation Id";

    /** Error user migration failed message */
    String ERROR_USER_MIGRATION_FAILED = "User migration failed.";

    /** Error user update password message */
    String ERROR_USER_UPDATE_PASSWORD = "User is created but password couldn't be updated.";

    // ==================== Format Utility Messages ====================

    /** OR format message */
    String OR_FORMAT = "{0} or {1}";

    /** AND format message */
    String AND_FORMAT = "{0} and {1}";

    /** Parameter not match message */
    String PARAM_NOT_MATCH = "%s-NOT-MATCH";

    // ==================== Error Messages (Legacy) ====================

    /** Forbidden message */
    String FORBIDDEN = "You are forbidden from accessing specified resource.";
  }

  /**
   * Nested interface containing all message key constants for localization and identification.
   * 
   * <p>These keys serve as unique identifiers for each message, enabling:
   * <ul>
   *   <li>Localization and multi-language support</li>
   *   <li>Client-side message resolution</li>
   *   <li>Error tracking and logging</li>
   * </ul>
   */
  interface Key {

    // ==================== General Messages ====================

    /** Success message key */
    String SUCCESS_MESSAGE = "0001";

    /** Error parameter exists key */
    String ERROR_PARAM_EXISTS = "0002";

    /** Data type error key */
    String DATA_TYPE_ERROR = "0003";

    /** Error duplicate entry key */
    String ERROR_DUPLICATE_ENTRY = "0004";

    /** Error attribute conflict key */
    String ERROR_ATTRIBUTE_CONFLICT = "0005";

    /** User account blocked key */
    String USER_ACCOUNT_BLOCKED = "0006";

    /** Data size exceeded key */
    String DATA_SIZE_EXCEEDED = "0007";

    /** User status message key */
    String USER_STATUS_MSG = "0008";

    /** Data format error key */
    String DATA_FORMAT_ERROR = "0009";

    /** Empty CSV file key */
    String EMPTY_CSV_FILE = "0010";

    /** Only email or phone or managed by required key */
    String ONLY_EMAIL_OR_PHONE_OR_MANAGEDBY_REQUIRED = "0011";

    /** Channel registration failed key */
    String CHANNEL_REG_FAILED = "0012";

    /** Resource not found key */
    String RESOURCE_NOT_FOUND = "0013";

    /** Max allowed size limit exceeded key */
    String MAX_ALLOWED_SIZE_LIMIT_EXCEED = "0014";

    /** Invalid parameter value key */
    String INVALID_PARAMETER_VALUE = "0017";

    /** Invalid value key */
    String INVALID_VALUE = "0018";

    /** Invalid parameter key */
    String INVALID_PARAMETER = "0019";

    /** Invalid columns key */
    String INVALID_COLUMNS = "0020";

    /** Invalid request parameter key */
    String INVALID_REQUEST_PARAMETER = "0021";

    /** Invalid request timeout key */
    String INVALID_REQUEST_TIMEOUT = "0022";

    /** Valid identifier absence key */
    String VALID_IDENTIFIER_ABSENSE = "0023";

    /** Invalid password key */
    String INVALID_PASSWORD = "0024";

    /** Invalid captcha key */
    String INVALID_CAPTCHA = "0025";

    /** Invalid consent status key */
    String INVALID_CONSENT_STATUS = "0026";

    /** Invalid operation name key */
    String INVALID_OPERATION_NAME = "0027";

    /** Invalid requested data key */
    String INVALID_REQUESTED_DATA = "0028";

    /** Invalid location delete request key */
    String INVALID_LOCATION_DELETE_REQUEST = "0029";

    /** Mandatory parameter missing key */
    String MANDATORY_PARAMETER_MISSING = "0030";

    /** Error mandatory parameter empty key */
    String ERROR_MANDATORY_PARAMETER_EMPTY = "0031";

    /** Error no framework found key */
    String ERROR_NO_FRAMEWORK_FOUND = "0032";

    /** Update not allowed key */
    String UPDATE_NOT_ALLOWED = "0033";

    /** Parent not allowed key */
    String PARENT_NOT_ALLOWED = "0034";

    /** Missing file attachment key */
    String MISSING_FILE_ATTACHMENT = "0035";

    /** File attachment size not configured key */
    String FILE_ATTACHMENT_SIZE_NOT_CONFIGURED = "0036";

    /** Empty file key */
    String EMPTY_FILE = "0037";

    /** Conflicting org locations key */
    String CONFLICTING_ORG_LOCATIONS = "0038";

    /** Empty header line key */
    String EMPTY_HEADER_LINE = "0039";

    /** Root org association error key */
    String ROOT_ORG_ASSOCIATION_ERROR = "0040";

    /** Dependent parameter missing key */
    String DEPENDENT_PARAMETER_MISSING = "0041";

    /** External ID assigned to other user key */
    String EXTERNALID_ASSIGNED_TO_OTHER_USER = "0042";

    /** Duplicate external IDs key */
    String DUPLICATE_EXTERNAL_IDS = "0043";

    /** Email recipients exceeded max limit key */
    String EMAIL_RECIPIENTS_EXCEEDS_MAX_LIMIT = "0044";

    /** Parameter mismatch key */
    String PARAMETER_MISMATCH = "0045";

    /** Error config load empty string key */
    String ERROR_CONFIG_LOAD_EMPTY_STRING = "0046";

    /** Error config load parse string key */
    String ERROR_CONFIG_LOAD_PARSE_STRING = "0047";

    /** Error config load empty config key */
    String ERROR_CONFIG_LOAD_EMPTY_CONFIG = "0048";

    /** Error no root org associated key */
    String ERROR_NO_ROOT_ORG_ASSOCIATED = "0049";

    /** Error unsupported cloud storage key */
    String ERROR_UNSUPPORTED_CLOUD_STORAGE = "0050";

    /** Error unsupported field key */
    String ERROR_UNSUPPORTED_FIELD = "0051";

    /** Invalid property error key */
    String INVALID_PROPERTY_ERROR = "0052";

    /** Error inactive org key */
    String ERROR_INACTIVE_ORG = "0053";

    /** Error duplicate entries key */
    String ERROR_DUPLICATE_ENTRIES = "0054";

    /** Error conflicting values key */
    String ERROR_CONFLICTING_VALUES = "0055";

    /** Error conflicting root org ID key */
    String ERROR_CONFLICTING_ROOT_ORG_ID = "0056";

    /** Error invalid OTP key */
    String ERROR_INVALID_OTP = "0057";

    /** Error invalid parameter size key */
    String ERROR_INVALID_PARAMETER_SIZE = "0058";

    /** Error rate limit exceeded key */
    String ERROR_RATE_LIMIT_EXCEEDED = "0059";

    /** Error user migration failed key */
    String ERROR_USER_MIGRATION_FAILED = "0060";

    /** Mandatory header parameter missing key */
    String MANDATORY_HEADER_PARAMETER_MISSING = "0061";

    /** Recovery parameter match exception key */
    String RECOVERY_PARAM_MATCH_EXCEPTION = "0062";

    /** OTP verification failed key */
    String OTP_VERIFICATION_FAILED = "0063";

    /** Service unavailable key */
    String SERVICE_UNAVAILABLE = "0064";

    /** Managed by not allowed key */
    String MANAGED_BY_NOT_ALLOWED = "0065";

    /** Managed user limit exceeded key */
    String MANAGED_USER_LIMIT_EXCEEDED = "0066";

    /** Declared user error status not updated key */
    String DECLARED_USER_ERROR_STATUS_IS_NOT_UPDATED = "0067";

    /** Declared user validated status not updated key */
    String DECLARED_USER_VALIDATED_STATUS_IS_NOT_UPDATED = "0068";

    /** Server error key */
    String SERVER_ERROR = JsonKey.USER_ORG_SERVICE_PREFIX + "0069";

    /** Unauthorized user key */
    String UNAUTHORIZED_USER = JsonKey.USER_ORG_SERVICE_PREFIX + "0070";

    /** Forbidden key */
    String FORBIDDEN = "0071";

    /** Invalid object type key */
    String INVALID_OBJECT_TYPE = "0072";

    /** Inactive user key */
    String INACTIVE_USER = "0073";

    /** Invalid CSV file key */
    String INVALID_CSV_FILE = "0074";

    /** Extended user profile not loaded key */
    String EXTENDED_USER_PROFILE_NOT_LOADED = "0075";

    /** Role processing invalid org key */
    String ROLE_PROCESSING_INVALID_ORG = "0076";

    /** Invalid file extension key */
    String INVALID_FILE_EXTENSION = "0077";

    /** Invalid encryption file key */
    String INVALID_ENCRYPTION_FILE = "0078";

    /** Invalid security level key */
    String INVALID_SECURITY_LEVEL = "0079";

    /** Invalid security level lower key */
    String INVALID_SECURITY_LEVEL_LOWER = "0080";

    /** Missing default security level key */
    String MISSING_DEFAULT_SECURITY_LEVEL = "0081";

    /** Invalid tenant security level lower key */
    String INVALID_TENANT_SECURITY_LEVEL_LOWER = "0082";

    /** Cannot delete user key */
    String CANNOT_DELETE_USER = "0083";

    /** Cannot transfer ownership key */
    String CANNOT_TRANSFER_OWNERSHIP = "0084";
  }
}

