package validators;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.request.Request;
import org.sunbird.util.ProjectUtil;
import org.sunbird.util.StringFormatter;
import org.sunbird.validator.EmailValidator;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Base validator class providing common validation methods for API requests.
 * Contains utilities for parameter validation, mandatory field checks, and data type verification.
 */
public class BaseRequestValidator {

    /**
     * Validates that a parameter value is not blank.
     *
     * @param value the parameter value to validate
     * @param error the error code to throw if validation fails
     * @throws ProjectCommonException if the value is blank
     */
    public void validateParam(String value, ResponseCode error) {
        if (StringUtils.isBlank(value)) {
            throw new ProjectCommonException(
                    error, error.getErrorMessage(), ResponseCode.CLIENT_ERROR.getResponseCode());
        }
    }

    /**
     * Validates that a parameter value is not blank with a custom error message argument.
     *
     * @param value the parameter value to validate
     * @param error the error code to throw if validation fails
     * @param errorMsgArgument the argument for formatting the error message
     * @throws ProjectCommonException if the value is blank
     */
    public void validateParam(String value, ResponseCode error, String errorMsgArgument) {
        if (StringUtils.isBlank(value)) {
            throw new ProjectCommonException(
                    error,
                    MessageFormat.format(error.getErrorMessage(), errorMsgArgument),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
    }

    /**
     * Checks that all specified mandatory fields are present in the data map.
     *
     * @param data the map containing request data
     * @param keys the mandatory field names to check
     * @throws ProjectCommonException if data is empty or any mandatory field is missing
     */
    public void checkMandatoryFieldsPresent(Map<String, Object> data, String... keys) {
        if (MapUtils.isEmpty(data)) {
            throw new ProjectCommonException(
                    ResponseCode.invalidRequestData,
                    ResponseCode.invalidRequestData.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
        Arrays.stream(keys)
                .forEach(
                        key -> {
                            if (StringUtils.isEmpty((String) data.get(key))) {
                                throw new ProjectCommonException(
                                        ResponseCode.mandatoryParamsMissing,
                                        ResponseCode.mandatoryParamsMissing.getErrorMessage(),
                                        ResponseCode.CLIENT_ERROR.getResponseCode(),
                                        key);
                            }
                        });
    }

    /**
     * Checks that all specified mandatory fields are present in the data map
     * and validates that each field value is of type String.
     *
     * @param data the map containing request data
     * @param mandatoryParamsList the list of mandatory field names to check
     * @throws ProjectCommonException if data is empty, any mandatory field is missing, or field is not a String
     */
    public void checkMandatoryFieldsPresent(
            Map<String, Object> data, List<String> mandatoryParamsList) {
        if (MapUtils.isEmpty(data)) {
            throw new ProjectCommonException(
                    ResponseCode.invalidRequestData,
                    ResponseCode.invalidRequestData.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
        mandatoryParamsList.forEach(
                key -> {
                    if (StringUtils.isEmpty((String) data.get(key))) {
                        throw new ProjectCommonException(
                                ResponseCode.mandatoryParamsMissing,
                                ResponseCode.mandatoryParamsMissing.getErrorMessage(),
                                ResponseCode.CLIENT_ERROR.getResponseCode(),
                                key);
                    }
                    if (!(data.get(key) instanceof String)) {
                        throw new ProjectCommonException(
                                ResponseCode.dataTypeError,
                                MessageFormat.format(ResponseCode.dataTypeError.getErrorMessage(), key, "String"),
                                ResponseCode.CLIENT_ERROR.getResponseCode());
                    }
                });
    }

    /**
     * Checks that specified read-only fields are not present in the data map.
     * Used to prevent updates to immutable fields.
     *
     * @param data the map containing request data
     * @param keys the read-only field names that must not be present
     * @throws ProjectCommonException if data is empty or any read-only field is present
     */
    public void checkReadOnlyAttributesAbsent(Map<String, Object> data, String... keys) {

        if (MapUtils.isEmpty(data)) {
            throw new ProjectCommonException(
                    ResponseCode.invalidRequestData,
                    ResponseCode.invalidRequestData.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
        Arrays.stream(keys)
                .forEach(
                        key -> {
                            if (data.containsKey(key)) {
                                throw new ProjectCommonException(
                                        ResponseCode.unupdatableField,
                                        ResponseCode.unupdatableField.getErrorMessage(),
                                        ResponseCode.CLIENT_ERROR.getResponseCode(),
                                        key);
                            }
                        });
    }

    /**
     * Validates that specified fields in the request map are of type List.
     *
     * @param requestMap the map containing request data
     * @param fieldPrefix the prefix to prepend to field names in error messages (can be null)
     * @param fields the field names to validate
     * @throws ProjectCommonException if any field is present but not a List
     */
    public void validateListParamWithPrefix(
            Map<String, Object> requestMap, String fieldPrefix, String... fields) {
        Arrays.stream(fields)
                .forEach(
                        field -> {
                            if (requestMap.containsKey(field)
                                    && null != requestMap.get(field)
                                    && !(requestMap.get(field) instanceof List)) {

                                String fieldWithPrefix =
                                        fieldPrefix != null ? StringFormatter.joinByDot(fieldPrefix, field) : field;

                                throw new ProjectCommonException(
                                        ResponseCode.dataTypeError,
                                        ProjectUtil.formatMessage(
                                                ResponseCode.dataTypeError.getErrorMessage(),
                                                fieldWithPrefix,
                                                JsonKey.LIST),
                                        ResponseCode.CLIENT_ERROR.getResponseCode());
                            }
                        });
    }

    /**
     * Validates that specified fields in the request map are of type List.
     *
     * @param requestMap the map containing request data
     * @param fields the field names to validate
     * @throws ProjectCommonException if any field is present but not a List
     */
    public void validateListParam(Map<String, Object> requestMap, String... fields) {
        validateListParamWithPrefix(requestMap, null, fields);
    }

    /**
     * Validates that the user ID in the request matches the authenticated user's ID.
     *
     * @param request the API request containing user information
     * @param userIdKey the key for the user ID field in the request
     * @throws ProjectCommonException if authentication is enabled and user IDs don't match
     */
    public static void validateUserId(Request request, String userIdKey) {
        if (ConfigFactory.load().getBoolean(JsonKey.AUTH_ENABLED) && !(request
                .getRequest()
                .get(userIdKey)
                .equals(request.getContext().get(JsonKey.REQUESTED_BY)))) {
            throw new ProjectCommonException(
                    ResponseCode.invalidParameterValue,
                    ResponseCode.invalidParameterValue.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode(),
                    (String) request.getRequest().get(JsonKey.USER_ID),
                    JsonKey.USER_ID);
        }
    }

    /**
     * Validates a search request, ensuring required filters are present and properly formatted.
     *
     * @param request the search request to validate
     * @throws ProjectCommonException if filters are missing or have invalid data types
     */
    public void validateSearchRequest(Request request) {
        if (null == request.getRequest().get(JsonKey.FILTERS)) {
            throw new ProjectCommonException(
                    ResponseCode.mandatoryParamsMissing,
                    MessageFormat.format(
                            ResponseCode.mandatoryParamsMissing.getErrorMessage(), JsonKey.FILTERS),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
        if (request.getRequest().containsKey(JsonKey.FILTERS)
                && (!(request.getRequest().get(JsonKey.FILTERS) instanceof Map))) {
            throw new ProjectCommonException(
                    ResponseCode.dataTypeError,
                    MessageFormat.format(
                            ResponseCode.dataTypeError.getErrorMessage(), JsonKey.FILTERS, "Map"),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
        validateSearchRequestFiltersValues(request);
        validateSearchRequestFieldsValues(request);
    }

    /**
     * Validates that the fields parameter in a search request is a list of strings.
     *
     * @param request the search request to validate
     * @throws ProjectCommonException if fields parameter has invalid data type
     */
    private void validateSearchRequestFieldsValues(Request request) {
        if (request.getRequest().containsKey(JsonKey.FIELDS)
                && (!(request.getRequest().get(JsonKey.FIELDS) instanceof List))) {
            throw new ProjectCommonException(
                    ResponseCode.dataTypeError,
                    MessageFormat.format(
                            ResponseCode.dataTypeError.getErrorMessage(), JsonKey.FIELDS, "List"),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
        if (request.getRequest().containsKey(JsonKey.FIELDS)
                && (request.getRequest().get(JsonKey.FIELDS) instanceof List)) {
            for (Object obj : (List) request.getRequest().get(JsonKey.FIELDS)) {
                if (!(obj instanceof String)) {
                    throw new ProjectCommonException(
                            ResponseCode.dataTypeError,
                            MessageFormat.format(
                                    ResponseCode.dataTypeError.getErrorMessage(), JsonKey.FIELDS, "List of String"),
                            ResponseCode.CLIENT_ERROR.getResponseCode());
                }
            }
        }
    }

    /**
     * Validates that filter values in a search request are properly formatted.
     *
     * @param request the search request to validate
     * @throws ProjectCommonException if filter values are invalid
     */
    private void validateSearchRequestFiltersValues(Request request) {
        if (request.getRequest().containsKey(JsonKey.FILTERS)
                && ((request.getRequest().get(JsonKey.FILTERS) instanceof Map))) {
            Map<String, Object> map = (Map<String, Object>) request.getRequest().get(JsonKey.FILTERS);

            map.forEach(
                    (key, val) -> {
                        if (key == null) {
                            throw new ProjectCommonException(
                                    ResponseCode.invalidParameterValue,
                                    MessageFormat.format(
                                            ResponseCode.invalidParameterValue.getErrorMessage(), key, JsonKey.FILTERS),
                                    ResponseCode.CLIENT_ERROR.getResponseCode());
                        }
                        if (val instanceof List) {
                            validateListValues((List) val, key);
                        } else if (val instanceof Map) {
                            validateMapValues((Map) val);
                        } else if (val == null)
                            if (StringUtils.isEmpty((String) val)) {
                                throw new ProjectCommonException(
                                        ResponseCode.invalidParameterValue,
                                        MessageFormat.format(
                                                ResponseCode.invalidParameterValue.getErrorMessage(), val, key),
                                        ResponseCode.CLIENT_ERROR.getResponseCode());
                            }
                    });
        }
    }

    /**
     * Validates that all values in a map are non-null.
     *
     * @param val the map to validate
     * @throws ProjectCommonException if any key or value is null
     */
    private void validateMapValues(Map val) {
        val.forEach(
                (k, v) -> {
                    if (k == null || v == null) {
                        throw new ProjectCommonException(
                                ResponseCode.invalidParameterValue,
                                MessageFormat.format(ResponseCode.invalidParameterValue.getErrorMessage(), v, k),
                                ResponseCode.CLIENT_ERROR.getResponseCode());
                    }
                });
    }

    /**
     * Validates that all values in a list are non-null.
     *
     * @param val the list to validate
     * @param key the key name for error reporting
     * @throws ProjectCommonException if any value is null
     */
    private void validateListValues(List val, String key) {
        val.forEach(
                v -> {
                    if (v == null) {
                        throw new ProjectCommonException(
                                ResponseCode.invalidParameterValue,
                                MessageFormat.format(ResponseCode.invalidParameterValue.getErrorMessage(), v, key),
                                ResponseCode.CLIENT_ERROR.getResponseCode());
                    }
                });
    }

    /**
     * Validates that an email address is in correct format.
     *
     * @param email the email address to validate
     * @throws ProjectCommonException if email format is invalid
     */
    public void validateEmail(String email) {
        if (!EmailValidator.isEmailValid(email)) {
            throw new ProjectCommonException(
                    ResponseCode.dataFormatError,
                    ResponseCode.dataFormatError.getErrorMessage(),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
    }

    /**
     * Validates that a phone number is in correct format.
     *
     * @param phone the phone number to validate
     * @throws ProjectCommonException if phone format is invalid
     */
    public void validatePhone(String phone) {
        if (!ProjectUtil.validatePhone(phone, null)) {
            throw new ProjectCommonException(
                    ResponseCode.dataFormatError,
                    String.format(ResponseCode.dataFormatError.getErrorMessage(), JsonKey.PHONE),
                    ResponseCode.CLIENT_ERROR.getResponseCode());
        }
    }

    /**
     * Creates and throws a client error exception with formatted message.
     *
     * @param responseCode the response code for the error
     * @param field the field name to include in the error message
     * @throws ProjectCommonException always throws this exception
     */
    public static void createClientError(ResponseCode responseCode, String field) {
        throw new ProjectCommonException(
                responseCode,
                ProjectUtil.formatMessage(responseCode.getErrorMessage(), field),
                ResponseCode.CLIENT_ERROR.getResponseCode());
    }
}

