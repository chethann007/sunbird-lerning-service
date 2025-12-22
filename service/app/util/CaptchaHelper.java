package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.utils.URIBuilder;
import org.sunbird.http.HttpClientUtil;
import org.sunbird.keys.JsonKey;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.util.ProjectUtil;

/**
 * Validates Google reCAPTCHA responses for application security.
 *
 * <p>This class handles validation of CAPTCHA tokens from both mobile and portal applications
 * by communicating with Google's reCAPTCHA verification service.
 */
public class CaptchaHelper {

    private static final LoggerUtil logger = new LoggerUtil(CaptchaHelper.class);

    private String captchaUrl;
    private String mobilePrivateKey;
    private String portalPrivateKey;
    private ObjectMapper mapper;

    /** Initializes the CAPTCHA helper with configuration from project settings. */
    public CaptchaHelper() {
        captchaUrl = "https://www.google.com/recaptcha/api/siteverify";
        mobilePrivateKey = ProjectUtil.getConfigValue(JsonKey.GOOGLE_CAPTCHA_MOBILE_PRIVATE_KEY);
        portalPrivateKey = ProjectUtil.getConfigValue(JsonKey.GOOGLE_CAPTCHA_PRIVATE_KEY);
        mapper = new ObjectMapper();
    }

    /**
     * Validates a CAPTCHA token against Google's verification service.
     *
     * @param captcha the CAPTCHA response token to validate
     * @param mobileApp application identifier; if non-empty, uses mobile private key; otherwise
     *     uses portal private key
     * @return true if CAPTCHA validation is successful; false otherwise
     */
    public boolean validate(String captcha, String mobileApp) {
        boolean isCaptchaValid = false;
        String url;
        Map<String, String> requestMap = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        try {
            // Select appropriate private key based on application type
            String secret =
                    StringUtils.isNotEmpty(mobileApp) ? mobilePrivateKey : portalPrivateKey;

            // Build verification URL
            url =
                    new URIBuilder(captchaUrl)
                            .addParameter(JsonKey.RESPONSE, captcha)
                            .addParameter("secret", secret)
                            .build()
                            .toString();

            // Prepare request
            requestMap.put(JsonKey.RESPONSE, captcha);
            requestMap.put("secret", secret);
            headers.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

            logger.info("Validating CAPTCHA with URL: " + url);

            // Call Google verification API
            String response = HttpClientUtil.postFormData(url, requestMap, headers, null);
            Map<String, Object> responseMap = mapper.readValue(response, Map.class);
            isCaptchaValid = (boolean) responseMap.get("success");

            // Log validation errors if any
            if (!isCaptchaValid) {
                @SuppressWarnings("unchecked")
                List<String> errorList = (List<String>) responseMap.get("error-codes");
                logger.warn(
                        "CAPTCHA validation failed with errors: "
                                + Arrays.toString(errorList.toArray()));
            }
        } catch (Exception e) {
            logger.error("Error validating CAPTCHA", e);
        }

        return isCaptchaValid;
    }
}

