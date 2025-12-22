package org.sunbird.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.request.Request;
import org.sunbird.request.RequestContext;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing common functionality across the application.
 * 
 * <p>This class provides a comprehensive set of utilities including:
 * <ul>
 *   <li>Date formatting and validation</li>
 *   <li>Email and phone number validation</li>
 *   <li>Unique ID generation</li>
 *   <li>Configuration and property management</li>
 *   <li>Object conversion and mapping</li>
 *   <li>Exception handling utilities</li>
 *   <li>Velocity template context building</li>
 * </ul>
 * 
 * <p>Also defines several enumerations for status codes, environment types,
 * and other application constants.
 */
public class ProjectUtil {

  /** Logger instance for this class */
  private static LoggerUtil logger = new LoggerUtil(ProjectUtil.class);

  /** Atomic counter for unique ID generation */
  private static AtomicInteger atomicInteger = new AtomicInteger();

  /** Standard date format: yyyy-MM-dd */
  public static final String YEAR_MONTH_DATE_FORMAT = "yyyy-MM-dd";

  /** Properties cache instance */
  public static PropertiesCache propertiesCache;

  /** Pattern matcher for email validation */
  private static Pattern pattern;

  /** Regular expression pattern for email validation */
  public static final String EMAIL_PATTERN =
      "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
          + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

  /** Fields to exclude from certain operations */
  public static final String[] excludes =
      new String[] {
        JsonKey.COMPLETENESS, JsonKey.MISSING_FIELDS, JsonKey.PROFILE_VISIBILITY, JsonKey.LOGIN_ID
      };

  /** JSON object mapper for serialization/deserialization */
  private static ObjectMapper mapper = new ObjectMapper();

  static {
    pattern = Pattern.compile(EMAIL_PATTERN);
    propertiesCache = PropertiesCache.getInstance();
  }

  /**
   * Environment type enumeration.
   */
  public enum Environment {
    /** Development environment */
    dev(1),
    /** Quality assurance environment */
    qa(2),
    /** Production environment */
    prod(3);

    /** Environment value */
    int value;

    /**
     * Constructor for Environment enum.
     *
     * @param value the environment value
     */
    Environment(int value) {
      this.value = value;
    }

    /**
     * Gets the numeric value of the environment.
     *
     * @return the environment value
     */
    public int getValue() {
      return value;
    }
  }

  /**
   * User/entity status enumeration.
   */
  public enum Status {
    /** Active status */
    ACTIVE(1),
    /** Inactive status */
    INACTIVE(0),
    /** Deleted status */
    DELETED(2);

    /** Status value */
    private int value;

    /**
     * Constructor for Status enum.
     *
     * @param value the status value
     */
    Status(int value) {
      this.value = value;
    }

    /**
     * Gets the numeric value of the status.
     *
     * @return the status value
     */
    public int getValue() {
      return this.value;
    }
  }

  /**
   * Bulk process status enumeration.
   */
  public enum BulkProcessStatus {
    /** New/initial status */
    NEW(0),
    /** Currently in progress */
    IN_PROGRESS(1),
    /** Processing interrupted */
    INTERRUPT(2),
    /** Processing completed */
    COMPLETED(3),
    /** Processing failed */
    FAILED(9);

    /** Status value */
    private int value;

    /**
     * Constructor for BulkProcessStatus enum.
     *
     * @param value the status value
     */
    BulkProcessStatus(int value) {
      this.value = value;
    }

    /**
     * Gets the numeric value of the bulk process status.
     *
     * @return the status value
     */
    public int getValue() {
      return this.value;
    }
  }

  /**
   * Organization status enumeration.
   */
  public enum OrgStatus {
    /** Inactive organization */
    INACTIVE(0),
    /** Active organization */
    ACTIVE(1),
    /** Blocked organization */
    BLOCKED(2),
    /** Retired organization */
    RETIRED(3);

    /** Status value */
    private Integer value;

    /**
     * Constructor for OrgStatus enum.
     *
     * @param value the status value
     */
    OrgStatus(Integer value) {
      this.value = value;
    }

    /**
     * Gets the numeric value of the organization status.
     *
     * @return the status value
     */
    public Integer getValue() {
      return this.value;
    }
  }

  /**
   * Progress tracking status enumeration.
   */
  public enum ProgressStatus {
    /** Not started */
    NOT_STARTED(0),
    /** Currently started */
    STARTED(1),
    /** Completed */
    COMPLETED(2);

    /** Status value */
    private int value;

    /**
     * Constructor for ProgressStatus enum.
     *
     * @param value the status value
     */
    ProgressStatus(int value) {
      this.value = value;
    }

    /**
     * Gets the numeric value of the progress status.
     *
     * @return the status value
     */
    public int getValue() {
      return this.value;
    }
  }

  /**
   * Active/Inactive status enumeration using boolean values.
   */
  public enum ActiveStatus {
    /** Active status */
    ACTIVE(true),
    /** Inactive status */
    INACTIVE(false);

    /** Status value */
    private boolean value;

    /**
     * Constructor for ActiveStatus enum.
     *
     * @param value the status value
     */
    ActiveStatus(boolean value) {
      this.value = value;
    }

    /**
     * Gets the boolean value of the active status.
     *
     * @return the status value
     */
    public boolean getValue() {
      return this.value;
    }
  }

  /**
   * User lookup type enumeration.
   */
  public enum UserLookupType {
    /** Look up by username */
    USERNAME(JsonKey.USER_LOOKUP_FILED_USER_NAME),
    /** Look up by email */
    EMAIL(JsonKey.EMAIL),
    /** Look up by phone */
    PHONE(JsonKey.PHONE);

    /** Lookup type string */
    private String type;

    /**
     * Constructor for UserLookupType enum.
     *
     * @param type the lookup type string
     */
    UserLookupType(String type) {
      this.type = type;
    }

    /**
     * Gets the lookup type string.
     *
     * @return the type string
     */
    public String getType() {
      return this.type;
    }
  }

  /**
   * User role enumeration.
   */
  public enum UserRole {
    /** Public user role */
    PUBLIC("PUBLIC");

    /** Role value */
    private String value;

    /**
     * Constructor for UserRole enum.
     *
     * @param value the role value
     */
    UserRole(String value) {
      this.value = value;
    }

    /**
     * Gets the role value.
     *
     * @return the role value
     */
    public String getValue() {
      return this.value;
    }
  }

  /**
   * HTTP method enumeration.
   */
  public enum Method {
    /** GET request */
    GET,
    /** POST request */
    POST,
    /** PUT request */
    PUT,
    /** DELETE request */
    DELETE,
    /** PATCH request */
    PATCH
  }

  /**
   * Elasticsearch type name enumeration.
   */
  public enum EsType {
    /** User type */
    user(getConfigValue(JsonKey.ES_USER_INDEX_ALIAS)),
    /** Organization type */
    organisation(getConfigValue(JsonKey.ES_ORG_INDEX_INDEX)),
    /** User notes type */
    usernotes(getConfigValue(JsonKey.ES_USER_NOTES_INDEX)),
    /** Location type */
    location(getConfigValue(JsonKey.ES_LOCATION_INDEX)),
    /** User feed type */
    userfeed(getConfigValue(JsonKey.ES_USER_FEED_INDEX));

    /** Type name */
    private String typeName;

    /**
     * Constructor for EsType enum.
     *
     * @param name the type name
     */
    EsType(String name) {
      this.typeName = name;
    }

    /**
     * Gets the Elasticsearch type name.
     *
     * @return the type name
     */
    public String getTypeName() {
      return typeName;
    }
  }

  /**
   * Report tracking status enumeration.
   */
  public enum ReportTrackingStatus {
    /** New report */
    NEW(0),
    /** Generating data for report */
    GENERATING_DATA(1),
    /** Uploading file */
    UPLOADING_FILE(2),
    /** File upload successful */
    UPLOADING_FILE_SUCCESS(3),
    /** Sending email */
    SENDING_MAIL(4),
    /** Email sent successfully */
    SENDING_MAIL_SUCCESS(5),
    /** Report generation failed */
    FAILED(9);

    /** Status value */
    private int value;

    /**
     * Constructor for ReportTrackingStatus enum.
     *
     * @param value the status value
     */
    ReportTrackingStatus(int value) {
      this.value = value;
    }

    /**
     * Gets the numeric value of the report tracking status.
     *
     * @return the status value
     */
    public int getValue() {
      return this.value;
    }
  }

  /**
   * Migration action enumeration.
   */
  public enum MigrateAction {
    /** Accept migration */
    ACCEPT("accept"),
    /** Reject migration */
    REJECT("reject");

    /** Action value */
    private String value;

    /**
     * Constructor for MigrateAction enum.
     *
     * @param value the action value
     */
    MigrateAction(String value) {
      this.value = value;
    }

    /**
     * Gets the action value.
     *
     * @return the action value
     */
    public String getValue() {
      return value;
    }
  }

  // ==================== Utility Methods ====================

  /**
   * Gets the current date formatted as "yyyy-MM-dd HH:mm:ss:SSSZ".
   *
   * @return the formatted current date
   */
  public static String getFormattedDate() {
    return getDateFormatter().format(new Date());
  }

  /**
   * Validates an email address using a regular expression pattern.
   *
   * @param email the email address to validate
   * @return true if valid email format, false otherwise
   */
  public static boolean isEmailvalid(final String email) {
    if (StringUtils.isBlank(email)) {
      return false;
    }
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  /**
   * Generates a unique ID based on timestamp and random values.
   *
   * @param environmentId the environment identifier
   * @return a unique ID string
   */
  public static String getUniqueIdFromTimestamp(int environmentId) {
    Random random = new Random();
    long env = (environmentId + random.nextInt(99999)) / 10000000;
    long uid = System.currentTimeMillis() + random.nextInt(999999);
    uid = uid << 13;
    return env + "" + uid + "" + atomicInteger.getAndIncrement();
  }

  /**
   * Generates a unique ID using UUID.
   *
   * @return a unique ID string based on UUID
   */
  public static synchronized String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Formats a message using MessageFormat.
   *
   * @param exceptionMsg the message template
   * @param fieldValue the values to substitute
   * @return the formatted message
   */
  public static String formatMessage(String exceptionMsg, Object... fieldValue) {
    return MessageFormat.format(exceptionMsg, fieldValue);
  }

  /**
   * Gets a SimpleDateFormat configured for "yyyy-MM-dd HH:mm:ss:SSSZ" format.
   *
   * @return configured SimpleDateFormat
   */
  public static SimpleDateFormat getDateFormatter() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSZ");
    simpleDateFormat.setLenient(false);
    return simpleDateFormat;
  }

  /**
   * Builds a VelocityContext from a map of variables for template processing.
   * Handles various email template variables including course, batch, user, and org data.
   *
   * @param map the map containing template variables
   * @return a configured VelocityContext
   */
  public static VelocityContext getContext(Map<String, Object> map) {
    propertiesCache = PropertiesCache.getInstance();
    VelocityContext context = new VelocityContext();
    if (StringUtils.isNotBlank((String) map.get(JsonKey.ACTION_URL))) {
      context.put(JsonKey.ACTION_URL, getValue(map, JsonKey.ACTION_URL));
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.NAME))) {
      context.put(JsonKey.NAME, getValue(map, JsonKey.NAME));
    }
    context.put(JsonKey.BODY, getValue(map, JsonKey.BODY));
    String fromEmail = getFromEmail(map);
    if (StringUtils.isNotBlank(fromEmail)) {
      context.put(JsonKey.FROM_EMAIL, fromEmail);
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.ORG_NAME))) {
      context.put(JsonKey.ORG_NAME, getValue(map, JsonKey.ORG_NAME));
    }
    String logoUrl = getSunbirdLogoUrl(map);
    if (StringUtils.isNotBlank(logoUrl)) {
      context.put(JsonKey.ORG_IMAGE_URL, logoUrl);
    }
    context.put(JsonKey.ACTION_NAME, getValue(map, JsonKey.ACTION_NAME));
    context.put(JsonKey.USERNAME, getValue(map, JsonKey.USERNAME));
    context.put(JsonKey.TEMPORARY_PASSWORD, getValue(map, JsonKey.TEMPORARY_PASSWORD));

    if (StringUtils.isNotBlank((String) map.get(JsonKey.COURSE_NAME))) {
      context.put(JsonKey.COURSE_NAME, map.remove(JsonKey.COURSE_NAME));
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.START_DATE))) {
      context.put(JsonKey.BATCH_START_DATE, map.remove(JsonKey.START_DATE));
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.END_DATE))) {
      context.put(JsonKey.BATCH_END_DATE, map.remove(JsonKey.END_DATE));
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.BATCH_NAME))) {
      context.put(JsonKey.BATCH_NAME, map.remove(JsonKey.BATCH_NAME));
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.FIRST_NAME))) {
      context.put(JsonKey.NAME, map.remove(JsonKey.FIRST_NAME));
    } else {
      context.put(JsonKey.NAME, "");
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.SIGNATURE))) {
      context.put(JsonKey.SIGNATURE, map.remove(JsonKey.SIGNATURE));
    }
    if (StringUtils.isNotBlank((String) map.get(JsonKey.COURSE_BATCH_URL))) {
      context.put(JsonKey.COURSE_BATCH_URL, map.remove(JsonKey.COURSE_BATCH_URL));
    }
    context.put(JsonKey.ALLOWED_LOGIN, propertiesCache.getProperty(JsonKey.SUNBIRD_ALLOWED_LOGIN));
    map = addCertStaticResource(map);
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      context.put(entry.getKey(), entry.getValue());
    }
    return context;
  }

  /**
   * Gets the Sunbird logo URL from the map or configuration.
   *
   * @param map the data map
   * @return the logo URL
   */
  private static String getSunbirdLogoUrl(Map<String, Object> map) {
    String logoUrl = (String) getValue(map, JsonKey.ORG_IMAGE_URL);
    if (StringUtils.isBlank(logoUrl)) {
      logoUrl = getConfigValue(JsonKey.SUNBIRD_ENV_LOGO_URL);
    }
    return logoUrl;
  }

  /**
   * Adds certificate static resources to the map.
   *
   * @param map the data map
   * @return the map with added static resources
   */
  private static Map<String, Object> addCertStaticResource(Map<String, Object> map) {
    map.putIfAbsent(
        JsonKey.certificateImgUrl,
        ProjectUtil.getConfigValue(JsonKey.SUNBIRD_CERT_COMPLETION_IMG_URL));
    map.putIfAbsent(
        JsonKey.dikshaImgUrl, ProjectUtil.getConfigValue(JsonKey.SUNBIRD_DIKSHA_IMG_URL));
    map.putIfAbsent(JsonKey.stateImgUrl, ProjectUtil.getConfigValue(JsonKey.SUNBIRD_STATE_IMG_URL));
    return map;
  }

  /**
   * Gets the from email address from the map or configuration.
   *
   * @param map the data map
   * @return the from email address
   */
  private static String getFromEmail(Map<String, Object> map) {
    String fromEmail = (String) getValue(map, JsonKey.EMAIL_SERVER_FROM);
    if (StringUtils.isBlank(fromEmail)) {
      fromEmail = getConfigValue(JsonKey.EMAIL_SERVER_FROM);
    }
    return fromEmail;
  }

  /**
   * Gets a value from the map and removes it.
   *
   * @param map the data map
   * @param key the key to retrieve
   * @return the value associated with the key
   */
  private static Object getValue(Map<String, Object> map, String key) {
    Object value = map.get(key);
    map.remove(key);
    return value;
  }

  /**
   * Creates a health check response map for a service.
   *
   * @param serviceName the name of the service
   * @param isError whether an error occurred
   * @param e the exception (if any)
   * @return a map containing health check status
   */
  public static Map<String, Object> createCheckResponse(
      String serviceName, boolean isError, Exception e) {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put(JsonKey.NAME, serviceName);
    if (!isError) {
      responseMap.put(JsonKey.Healthy, true);
      responseMap.put(JsonKey.ERROR, "");
      responseMap.put(JsonKey.ERRORMSG, "");
    } else {
      responseMap.put(JsonKey.Healthy, false);
      if (e != null && e instanceof ProjectCommonException) {
        ProjectCommonException commonException = (ProjectCommonException) e;
        responseMap.put(JsonKey.ERROR, commonException.getErrorResponseCode());
        responseMap.put(JsonKey.ERRORMSG, commonException.getMessage());
      } else {
        responseMap.put(JsonKey.ERROR, e != null ? e.getMessage() : "CONNECTION_ERROR");
        responseMap.put(JsonKey.ERRORMSG, e != null ? e.getMessage() : "Connection error");
      }
    }
    return responseMap;
  }

  /**
   * Sets trace ID and request ID in the response header from the request context.
   *
   * @param header the header map to update
   * @param context the request context
   */
  public static void setTraceIdInHeader(Map<String, String> header, RequestContext context) {
    if (null != context) {
      header.put(JsonKey.X_TRACE_ENABLED, context.getDebugEnabled());
      header.put(JsonKey.X_REQUEST_ID, context.getReqId());
    }
  }

  /**
   * Validates a phone number using the libphonenumber library.
   *
   * @param phNumber the phone number to validate
   * @param countryCode the country code
   * @return true if the phone number is valid, false otherwise
   */
  public static boolean validatePhone(String phNumber, String countryCode) {
    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    String contryCode = countryCode;
    if (!StringUtils.isBlank(countryCode) && (countryCode.charAt(0) != '+')) {
      contryCode = "+" + countryCode;
    }
    Phonenumber.PhoneNumber phoneNumber = null;
    try {
      if (StringUtils.isBlank(countryCode)) {
        contryCode = PropertiesCache.getInstance().getProperty("sunbird_default_country_code");
      }
      String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(contryCode));
      phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
      return phoneNumberUtil.isValidNumber(phoneNumber);
    } catch (NumberParseException e) {
      logger.error(phNumber + " :this phone no. is not a valid one.", e);
    }
    return false;
  }

  /**
   * Validates a country code format.
   *
   * @param countryCode the country code to validate
   * @return true if the country code format is valid, false otherwise
   */
  public static boolean validateCountryCode(String countryCode) {
    String pattern = "^(?:[+] ?){0,1}(?:[0-9] ?){1,3}";
    try {
      Pattern patt = Pattern.compile(pattern);
      Matcher matcher = patt.matcher(countryCode);
      return matcher.matches();
    } catch (RuntimeException e) {
      return false;
    }
  }

  /**
   * Validates a UUID string.
   *
   * @param uuidStr the UUID string to validate
   * @return true if the string is a valid UUID, false otherwise
   */
  public static boolean validateUUID(String uuidStr) {
    try {
      UUID.fromString(uuidStr);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * Validates a date string against a specified format.
   *
   * @param format the expected date format
   * @param value the date string to validate
   * @return true if the date string matches the format, false otherwise
   */
  public static boolean isDateValidFormat(String format, String value) {
    Date date = null;
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      date = sdf.parse(value);
      if (!value.equals(sdf.format(date))) {
        date = null;
      }
    } catch (ParseException ex) {
      logger.error("isDateValidFormat: " + ex.getMessage(), ex);
    }
    return date != null;
  }

  /**
   * Gets a configuration value from environment variables or properties cache.
   * Environment variables take precedence.
   *
   * @param key the configuration key
   * @return the configuration value, or null if not found
   */
  public static String getConfigValue(String key) {
    if (StringUtils.isNotBlank(System.getenv(key))) {
      return System.getenv(key);
    }
    return propertiesCache.readProperty(key);
  }

  /**
   * Checks if a string array contains only non-empty strings.
   *
   * @param strArray the string array to check
   * @return true if array is empty or all strings are empty, false if at least one non-empty string found
   */
  public static boolean isNotEmptyStringArray(String[] strArray) {
    for (String str : strArray) {
      if (StringUtils.isNotEmpty(str)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Converts a list of maps to a JSON string.
   *
   * @param mapList the list of maps to convert
   * @return the JSON string representation, or null if conversion fails
   */
  public static String convertMapToJsonString(List<Map<String, Object>> mapList) {
    try {
      return mapper.writeValueAsString(mapList);
    } catch (IOException e) {
      logger.error("convertMapToJsonString : " + e.getMessage(), e);
    }
    return null;
  }

  /**
   * Removes specified keys from a map.
   *
   * @param map the map to modify
   * @param keys the keys to remove
   */
  public static void removeUnwantedFields(Map<String, Object> map, String... keys) {
    Arrays.stream(keys).forEach(map::remove);
  }

  /**
   * Converts a Request object's data to a POJO of the specified type.
   *
   * @param request the request object
   * @param clazz the target POJO class
   * @param <T> the target type
   * @return an instance of the target type populated from request data
   */
  public static <T> T convertToRequestPojo(Request request, Class<T> clazz) {
    return mapper.convertValue(request.getRequest(), clazz);
  }

  /**
   * Creates a ProjectCommonException for a client error using the provided response code.
   *
   * @param responseCode the response code enum
   * @return a ProjectCommonException configured for client error
   */
  public static ProjectCommonException createClientException(ResponseCode responseCode) {
    return new ProjectCommonException(
        responseCode, responseCode.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
  }

  /**
   * Creates a ProjectCommonException for a client error with a custom message.
   *
   * @param responseCode the response code enum
   * @param exceptionMessage the custom error message
   * @return a ProjectCommonException configured for client error
   */
  public static ProjectCommonException createClientException(
      ResponseCode responseCode, String exceptionMessage) {
    return new ProjectCommonException(
        responseCode,
        StringUtils.isBlank(exceptionMessage) ? responseCode.getErrorMessage() : exceptionMessage,
        ResponseCode.CLIENT_ERROR.getResponseCode());
  }

  /**
   * Throws a ProjectCommonException for a client error with a custom message.
   *
   * @param responseCode the response code enum
   * @param exceptionMessage the custom error message
   * @throws ProjectCommonException always
   */
  public static void throwClientErrorException(ResponseCode responseCode, String exceptionMessage) {
    throw new ProjectCommonException(
        responseCode,
        StringUtils.isBlank(exceptionMessage) ? responseCode.getErrorMessage() : exceptionMessage,
        ResponseCode.CLIENT_ERROR.getResponseCode());
  }

  /**
   * Extracts the LMS user ID from a federated user ID by removing the federation prefix.
   *
   * @param fedUserId the federated user ID
   * @return the LMS user ID without the federation prefix
   */
  public static String getLmsUserId(String fedUserId) {
    String userId = fedUserId;
    String prefix =
        "f:" + getConfigValue(JsonKey.SUNBIRD_KEYCLOAK_USER_FEDERATION_PROVIDER_ID) + ":";
    if (StringUtils.isNotBlank(fedUserId) && fedUserId.startsWith(prefix)) {
      userId = fedUserId.replace(prefix, "");
    }
    return userId;
  }

  /**
   * Extracts the first N characters from a string.
   *
   * @param originalText the original text
   * @param noOfChar the number of characters to extract
   * @return the first N characters, or the full string if it's shorter than N
   */
  public static String getFirstNCharacterString(String originalText, int noOfChar) {
    if (StringUtils.isBlank(originalText)) {
      return "";
    }
    String firstNChars = "";
    if (originalText.length() > noOfChar) {
      firstNChars = originalText.substring(0, noOfChar);
    } else {
      firstNChars = originalText;
    }
    return firstNChars;
  }
}

