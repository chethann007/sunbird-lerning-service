package validators.usermanagement;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.exception.ProjectCommonException;
import org.sunbird.exception.ResponseCode;
import org.sunbird.exception.ResponseMessage;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.request.Request;
import org.sunbird.request.RequestContext;
import org.sunbird.util.ProjectUtil;
import org.sunbird.util.StringFormatter;
import org.sunbird.util.cache.DataCacheHandler;
import org.sunbird.util.form.FormApiUtil;
import validators.BaseRequestValidator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Validator for user management requests providing validation for user creation,
 * external IDs, user types, phone numbers, passwords, and date of birth.
 */
public class UserRequestValidator extends BaseRequestValidator {

    private final int ERROR_CODE = ResponseCode.CLIENT_ERROR.getResponseCode();
    protected static List<String> typeList = new ArrayList<>();
    private static final LoggerUtil logger = new LoggerUtil(UserRequestValidator.class);

    static {
        List<String> subTypeList =
                Arrays.asList(ProjectUtil.getConfigValue(JsonKey.SUNBIRD_VALID_LOCATION_TYPES).split(";"));
        for (String str : subTypeList) {
            typeList.addAll(
                    ((Arrays.asList(str.split(",")))
                            .stream()
                            .map(x -> x.toLowerCase()))
                            .collect(Collectors.toList()));
        }
    }

    /**
     * Validates a create user request including external IDs, user type, phone, and password.
     * 
     * @param userRequest the user creation request to validate
     */
    public void validateCreateUserRequest(Request userRequest) {
        externalIdsValidation(userRequest, JsonKey.CREATE);
        fieldsNotAllowed(
                Arrays.asList(
                        JsonKey.REGISTERED_ORG_ID,
                        JsonKey.ROOT_ORG_ID,
                        JsonKey.PROVIDER,
                        JsonKey.EXTERNAL_ID,
                        JsonKey.EXTERNAL_ID_PROVIDER,
                        JsonKey.EXTERNAL_ID_TYPE,
                        JsonKey.ID_TYPE,
                        JsonKey.PROFILE_USERTYPES),
                userRequest);
        createUserBasicValidation(userRequest);
        validateUserType(userRequest.getRequest(), null, userRequest.getRequestContext());
        phoneValidation(userRequest);
        validatePassword((String) userRequest.getRequest().get(JsonKey.PASSWORD));
    }

    /**
     * Validates external IDs in the user request for the specified operation.
     * 
     * @param userRequest the user request containing external IDs
     * @param operation the operation type (CREATE, UPDATE, etc.)
     */
    public void externalIdsValidation(Request userRequest, String operation) {
        if (userRequest.getRequest().containsKey(JsonKey.EXTERNAL_IDS)
                && (null != userRequest.getRequest().get(JsonKey.EXTERNAL_IDS))) {
            if (!(userRequest.getRequest().get(JsonKey.EXTERNAL_IDS) instanceof List)) {
                throw new ProjectCommonException(
                        ResponseCode.dataTypeError,
                        ProjectUtil.formatMessage(
                                ResponseCode.dataTypeError.getErrorMessage(), JsonKey.EXTERNAL_IDS, JsonKey.LIST),
                        ERROR_CODE);
            }
            List<Map<String, String>> externalIds =
                    (List<Map<String, String>>) userRequest.getRequest().get(JsonKey.EXTERNAL_IDS);
            validateIndividualExternalId(operation, externalIds);
            if (operation.equalsIgnoreCase(JsonKey.CREATE)) {
                checkForDuplicateExternalId(externalIds);
            }
        }
    }

    /**
     * Validates that specified fields are not present in the user request.
     * 
     * @param fields the list of field names that are not allowed
     * @param userRequest the user request to validate
     */
    public void fieldsNotAllowed(List<String> fields, Request userRequest) {
        for (String field : fields) {
            if (((userRequest.getRequest().get(field) instanceof String)
                    && StringUtils.isNotBlank((String) userRequest.getRequest().get(field)))
                    || (null != userRequest.getRequest().get(field))) {
                throw new ProjectCommonException(
                        ResponseCode.invalidRequestParameter,
                        ProjectUtil.formatMessage(
                                ResponseCode.invalidRequestParameter.getErrorMessage(), field),
                        ERROR_CODE);
            }
        }
    }

    /**
     * Validates basic user creation fields and roles.
     * 
     * @param userRequest the user creation request to validate
     */
    public void createUserBasicValidation(Request userRequest) {

        createUserBasicProfileFieldsValidation(userRequest);
        if (userRequest.getRequest().containsKey(JsonKey.ROLES)
                && null != userRequest.getRequest().get(JsonKey.ROLES)
                && !(userRequest.getRequest().get(JsonKey.ROLES) instanceof List)) {
            throw new ProjectCommonException(
                    ResponseCode.dataTypeError,
                    ProjectUtil.formatMessage(
                            ResponseCode.dataTypeError.getErrorMessage(), JsonKey.ROLES, JsonKey.LIST),
                    ERROR_CODE);
        }
    }

    /**
     * Validates user type against configuration for the specified state code.
     * 
     * @param userRequestMap the user request map containing user type
     * @param stateCode the state code for validation context
     * @param context the request context
     * @return the validated state code
     */
    public String validateUserType(Map<String, Object> userRequestMap, String stateCode, RequestContext context) {
        String userType = (String) userRequestMap.get(JsonKey.USER_TYPE);
        if (null != userType) {
            Map<String, Map<String, List<String>>> userTypeConfigMap =
                    DataCacheHandler.getUserTypesConfig();
            if (StringUtils.isBlank(stateCode)) {
                stateCode = JsonKey.DEFAULT_PERSONA;
            }

            if (!userTypeConfigMap.containsKey(stateCode)) {
                // Get profile data config
                Map<String, List<String>> userProfileConfigMap =
                        FormApiUtil.getUserTypeConfig(FormApiUtil.getProfileConfig(stateCode, context));
                if (MapUtils.isEmpty(userProfileConfigMap)) {
                    // Get Default Config
                    stateCode = JsonKey.DEFAULT_PERSONA;
                    userProfileConfigMap = userTypeConfigMap.get(stateCode);
                    if (MapUtils.isEmpty(userProfileConfigMap)) {
                        userProfileConfigMap =
                                FormApiUtil.getUserTypeConfig(FormApiUtil.getProfileConfig(stateCode, context));
                        if (MapUtils.isNotEmpty(userProfileConfigMap)) {
                            userTypeConfigMap.put(stateCode, userProfileConfigMap);
                        } else {
                            logger.info(
                                    context, String.format("Form Config not found for stateCode:%s", stateCode));
                        }
                    }
                } else {
                    userTypeConfigMap.put(stateCode, userProfileConfigMap);
                }
                logger.info("form config for state:" + stateCode + " " + userTypeConfigMap);
            }

            Map<String, List<String>> userTypeMap = userTypeConfigMap.get(stateCode);
            if (MapUtils.isEmpty(userTypeMap)) {
                ProjectCommonException.throwClientErrorException(
                        ResponseCode.SERVER_ERROR,
                        MessageFormat.format(ResponseMessage.Message.USER_TYPE_CONFIG_IS_EMPTY, stateCode));
            }
            logger.info(
                    context,
                    String.format(
                            "Available User Type for stateCode:%s are %s", stateCode, userTypeMap.keySet()));
            logger.info(
                    context,
                    String.format(
                            "Available User Type for stateCode:%s are %s", stateCode, userTypeMap.keySet()));
            List<Map> profileUserTypes = (List<Map>) userRequestMap.get(JsonKey.PROFILE_USERTYPES);
            if (CollectionUtils.isNotEmpty(profileUserTypes)
                    && MapUtils.isNotEmpty((Map) profileUserTypes.get(0))) {
                profileUserTypes.forEach(
                        item -> {
                            String userTypeItem = (String) item.get(JsonKey.TYPE);
                            if (!userTypeMap.containsKey(userTypeItem)) {
                                ProjectCommonException.throwClientErrorException(
                                        ResponseCode.invalidParameterValue,
                                        MessageFormat.format(
                                                ResponseCode.invalidParameterValue.getErrorMessage(),
                                                new String[] {userType, JsonKey.USER_TYPE}));
                            }
                        });
            } else if (!userTypeMap.containsKey(userType)) {
                ProjectCommonException.throwClientErrorException(
                        ResponseCode.invalidParameterValue,
                        MessageFormat.format(
                                ResponseCode.invalidParameterValue.getErrorMessage(),
                                new String[] {userType, JsonKey.USER_TYPE}));
            }
        }
        return stateCode;
    }

    /**
     * Validates phone number and country code in the user request.
     * 
     * @param userRequest the user request containing phone information
     */
    public void phoneValidation(Request userRequest) {
        if (!StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE))) {
            boolean bool =
                    ProjectUtil.validateCountryCode(
                            (String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE));
            if (!bool) {
                ProjectCommonException.throwClientErrorException(
                        ResponseCode.invalidParameter,
                        MessageFormat.format(
                                ResponseCode.invalidParameter.getErrorMessage(), JsonKey.COUNTRY_CODE_TEXT));
            }
        }
        if (StringUtils.isNotBlank((String) userRequest.getRequest().get(JsonKey.PHONE))) {
            validatePhoneNo(
                    (String) userRequest.getRequest().get(JsonKey.PHONE),
                    (String) userRequest.getRequest().get(JsonKey.COUNTRY_CODE));
        }
    }

    /**
     * Validates password format and strength.
     * 
     * @param password the password to validate
     */
    private void validatePassword(String password) {
        if (StringUtils.isNotBlank(password)) {
            boolean response = isGoodPassword(password);
            if (!response) {
                throw new ProjectCommonException(
                        ResponseCode.passwordValidation,
                        ResponseCode.passwordValidation.getErrorMessage(),
                        ERROR_CODE);
            }
        }
    }

    /**
     * Validates individual external ID entries for proper format and operation type.
     * 
     * @param operation the operation type (CREATE, UPDATE, etc.)
     * @param externalIds the list of external IDs to validate
     */
    private void validateIndividualExternalId(String operation, List<Map<String, String>> externalIds) {
        // valid operation type for externalIds in user api.
        List<String> operationTypeList = Arrays.asList(JsonKey.ADD, JsonKey.REMOVE, JsonKey.EDIT);
        externalIds
                .stream()
                .forEach(
                        identity -> {
                            // check for invalid operation type
                            if (StringUtils.isNotBlank(identity.get(JsonKey.OPERATION))
                                    && (!operationTypeList.contains(
                                    (identity.get(JsonKey.OPERATION)).toLowerCase()))) {
                                throw new ProjectCommonException(
                                        ResponseCode.invalidValue,
                                        ProjectUtil.formatMessage(
                                                ResponseCode.invalidValue.getErrorMessage(),
                                                StringFormatter.joinByDot(JsonKey.EXTERNAL_IDS, JsonKey.OPERATION),
                                                identity.get(JsonKey.OPERATION),
                                                String.join(StringFormatter.COMMA, operationTypeList)),
                                        ERROR_CODE);
                            }
                            // throw exception for invalid operation if other operation type is coming in
                            // request
                            // other than add or null for create user api
                            if (JsonKey.CREATE.equalsIgnoreCase(operation)
                                    && StringUtils.isNotBlank(identity.get(JsonKey.OPERATION))
                                    && (!JsonKey.ADD.equalsIgnoreCase(((identity.get(JsonKey.OPERATION)))))) {
                                throw new ProjectCommonException(
                                        ResponseCode.invalidValue,
                                        ProjectUtil.formatMessage(
                                                ResponseCode.invalidValue.getErrorMessage(),
                                                StringFormatter.joinByDot(JsonKey.EXTERNAL_IDS, JsonKey.OPERATION),
                                                identity.get(JsonKey.OPERATION),
                                                JsonKey.ADD),
                                        ERROR_CODE);
                            }
                            validateExternalIdMandatoryParam(JsonKey.ID, identity.get(JsonKey.ID));
                            validateExternalIdMandatoryParam(JsonKey.PROVIDER, identity.get(JsonKey.PROVIDER));
                            validateExternalIdMandatoryParam(JsonKey.ID_TYPE, identity.get(JsonKey.ID_TYPE));
                        });
    }

    /**
     * Checks for duplicate external IDs in the provided list.
     * 
     * @param list the list of external IDs to check for duplicates
     */
    private void checkForDuplicateExternalId(List<Map<String, String>> list) {
        List<Map<String, String>> checkedList = new ArrayList<>();
        for (Map<String, String> externalId : list) {
            for (Map<String, String> checkedExternalId : checkedList) {
                String provider = checkedExternalId.get(JsonKey.PROVIDER);
                String idType = checkedExternalId.get(JsonKey.ID_TYPE);
                if (provider.equalsIgnoreCase(externalId.get(JsonKey.PROVIDER))
                        && idType.equalsIgnoreCase(externalId.get(JsonKey.ID_TYPE))) {
                    String exceptionMsg =
                            MessageFormat.format(
                                    ResponseCode.duplicateExternalIds.getErrorMessage(), idType, provider);
                    ProjectCommonException.throwClientErrorException(
                            ResponseCode.duplicateExternalIds, exceptionMsg);
                }
            }
            checkedList.add(externalId);
        }
    }

    /**
     * Validates basic profile fields required for user creation.
     * 
     * @param userRequest the user request to validate
     */
    private void createUserBasicProfileFieldsValidation(Request userRequest) {
        validateParam(
                (String) userRequest.getRequest().get(JsonKey.FIRST_NAME),
                ResponseCode.mandatoryParamsMissing,
                JsonKey.FIRST_NAME);
        if (StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.EMAIL))
                && StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.PHONE))
                && StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.MANAGED_BY))) {
            ProjectCommonException.throwClientErrorException(
                    ResponseCode.invalidParameter,
                    MessageFormat.format(
                            ResponseCode.invalidParameter.getErrorMessage(),
                            JsonKey.EMAIL + " or " + JsonKey.PHONE));
        }

        if ((StringUtils.isNotBlank((String) userRequest.getRequest().get(JsonKey.EMAIL))
                || StringUtils.isNotBlank((String) userRequest.getRequest().get(JsonKey.PHONE)))
                && StringUtils.isNotBlank((String) userRequest.getRequest().get(JsonKey.MANAGED_BY))) {
            ProjectCommonException.throwClientErrorException(
                    ResponseCode.OnlyEmailorPhoneorManagedByRequired);
        }

        if ((null == userRequest.getRequest().get(JsonKey.DOB_VALIDATION_DONE))) {
            validateDob(userRequest);
        }

        if (!StringUtils.isBlank((String) userRequest.getRequest().get(JsonKey.EMAIL))
                && !ProjectUtil.isEmailvalid((String) userRequest.getRequest().get(JsonKey.EMAIL))) {
            ProjectCommonException.throwClientErrorException(
                    ResponseCode.dataFormatError,
                    MessageFormat.format(ResponseCode.dataFormatError.getErrorMessage(), JsonKey.EMAIL));
        }
    }

    /**
     * Validates phone number format and checks for invalid characters.
     * 
     * @param phone the phone number to validate
     * @param countryCode the country code for the phone number
     * @return true if phone number is valid, false otherwise
     */
    private boolean validatePhoneNo(String phone, String countryCode) {
        if (phone.contains("+")) {
            ProjectCommonException.throwClientErrorException(
                    ResponseCode.invalidParameter,
                    MessageFormat.format(ResponseCode.invalidParameter.getErrorMessage(), JsonKey.PHONE));
        }
        if (ProjectUtil.validatePhone(phone, countryCode)) {
            return true;
        } else {
            ProjectCommonException.throwClientErrorException(
                    ResponseCode.dataFormatError,
                    MessageFormat.format(ResponseCode.dataFormatError.getErrorMessage(), JsonKey.PHONE));
        }
        return false;
    }

    /**
     * Validates password strength against configured regex pattern.
     * 
     * @param password the password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isGoodPassword(String password) {
        return password.matches(ProjectUtil.getConfigValue(JsonKey.SUNBIRD_PASS_REGEX));
    }

    /**
     * Validates mandatory parameters for external IDs.
     * 
     * @param param the parameter name
     * @param paramValue the parameter value
     */
    private void validateExternalIdMandatoryParam(String param, String paramValue) {
        if (StringUtils.isBlank(paramValue)) {
            throw new ProjectCommonException(
                    ResponseCode.mandatoryParamsMissing,
                    ProjectUtil.formatMessage(
                            ResponseCode.mandatoryParamsMissing.getErrorMessage(),
                            StringFormatter.joinByDot(JsonKey.EXTERNAL_IDS, param)),
                    ERROR_CODE);
        }
    }

    /**
     * Validates date of birth format and converts it to the required format.
     * 
     * @param userRequest the user request containing date of birth
     */
    public void validateDob(Request userRequest) {
        if (null != userRequest.getRequest().get(JsonKey.DOB)) {
            String dobValue =
                    userRequest.getRequest().get(JsonKey.DOB)
                            + ProjectUtil.getConfigValue(JsonKey.DEFAULT_MONTH_DATE);
            boolean bool = ProjectUtil.isDateValidFormat(ProjectUtil.YEAR_MONTH_DATE_FORMAT, dobValue);
            if (!bool) {
                ProjectCommonException.throwClientErrorException(
                        ResponseCode.dataFormatError,
                        MessageFormat.format(ResponseCode.dataFormatError.getErrorMessage(), JsonKey.DOB));
            } else {
                userRequest.getRequest().put(JsonKey.DOB, dobValue);
                userRequest.getRequest().put(JsonKey.DOB_VALIDATION_DONE, true);
            }
        }
    }
}
