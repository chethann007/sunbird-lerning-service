package util;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.exception.ResponseCode;
import org.sunbird.keys.JsonKey;
import org.sunbird.response.ResponseParams;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;

/**
 * Common utility methods for HTTP request handling and response creation.
 *
 * <p>Provides helper methods for extracting attributes from HTTP requests and constructing
 * standardized response objects with appropriate status codes and messages.
 */
public class Common {

    /**
     * Retrieves an attribute value from an HTTP request.
     *
     * @param httpReq the HTTP request object
     * @param attribute the typed key for the attribute to retrieve
     * @return the attribute value, or null if not present
     */
    public static String getFromRequest(Http.Request httpReq, TypedKey<?> attribute) {
        if (httpReq != null && httpReq.attrs() != null && httpReq.attrs().containsKey(attribute)) {
            return (String) httpReq.attrs().get(attribute);
        }
        return null;
    }

    /**
     * Creates a response parameter object with status information.
     *
     * @param code the response code indicating success or failure
     * @param customMessage optional custom error message; uses code's default message if blank
     * @param requestId the request identifier to include in the response
     * @return a ResponseParams object configured based on the response code
     */
    public static ResponseParams createResponseParamObj(
            ResponseCode code, String customMessage, String requestId) {
        ResponseParams params = new ResponseParams();

        if (code.getResponseCode() != 200) {
            params.setErr(code.getErrorCode());
            params.setErrmsg(
                    StringUtils.isNotBlank(customMessage)
                            ? customMessage
                            : code.getErrorMessage());
            params.setStatus(JsonKey.FAILED);
        } else {
            params.setStatus(JsonKey.SUCCESS);
        }

        params.setResmsgid(requestId);
        params.setMsgid(requestId);
        return params;
    }

    private Common() {
        // Utility class, no instantiation
    }
}

