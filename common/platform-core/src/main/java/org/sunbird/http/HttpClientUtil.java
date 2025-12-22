package org.sunbird.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections.MapUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.sunbird.logging.LoggerUtil;
import org.sunbird.request.RequestContext;

/**
 * Utility class for making HTTP requests using Apache HttpClient with connection pooling.
 * Provides methods for GET, POST, PATCH, and DELETE operations.
 */
public class HttpClientUtil {
  private static final LoggerUtil logger = new LoggerUtil(HttpClientUtil.class);

  private static CloseableHttpClient httpclient = null;
  private static HttpClientUtil httpClientUtil;

  /**
   * Private constructor that initializes the HTTP client with connection pooling
   * and keep-alive strategy.
   */
  private HttpClientUtil() {
    ConnectionKeepAliveStrategy keepAliveStrategy =
        (response, context) -> {
          HeaderElementIterator it =
              new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
          while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
              return Long.parseLong(value) * 1000;
            }
          }
          return 180 * 1000;
        };

    PoolingHttpClientConnectionManager connectionManager =
        new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(200);
    connectionManager.setDefaultMaxPerRoute(150);
    connectionManager.closeIdleConnections(180, TimeUnit.SECONDS);
    httpclient =
        HttpClients.custom()
            .setConnectionManager(connectionManager)
            .useSystemProperties()
            .setKeepAliveStrategy(keepAliveStrategy)
            .build();
  }

  /**
   * Returns the singleton instance of HttpClientUtil.
   *
   * @return the HttpClientUtil instance
   */
  public static HttpClientUtil getInstance() {
    if (httpClientUtil == null) {
      synchronized (HttpClientUtil.class) {
        if (httpClientUtil == null) {
          httpClientUtil = new HttpClientUtil();
        }
      }
    }
    return httpClientUtil;
  }

  /**
   * Executes an HTTP GET request.
   *
   * @param requestURL the URL to send the request to
   * @param headers optional headers to include in the request
   * @param context the request context for logging
   * @return the response body as a string, or empty string on error
   */
  public static String get(String requestURL, Map<String, String> headers, RequestContext context) {
    CloseableHttpResponse response = null;
    try {
      HttpGet httpGet = new HttpGet(requestURL);
      if (MapUtils.isNotEmpty(headers)) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          httpGet.addHeader(entry.getKey(), entry.getValue());
        }
      }
      response = httpclient.execute(httpGet);
      return getResponse(response, context, "GET");
    } catch (Exception ex) {
      logger.error(context, "Exception occurred while calling get method", ex);
      return "";
    } finally {
      closeResponse(response, context, "GET");
    }
  }

  /**
   * Executes an HTTP POST request with JSON payload.
   *
   * @param requestURL the URL to send the request to
   * @param params the JSON string to send as request body
   * @param headers optional headers to include in the request
   * @param context the request context for logging
   * @return the response body as a string, or empty string on error
   */
  public static String post(
      String requestURL, String params, Map<String, String> headers, RequestContext context) {
    CloseableHttpResponse response = null;
    try {
      HttpPost httpPost = new HttpPost(requestURL);
      if (MapUtils.isNotEmpty(headers)) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          httpPost.addHeader(entry.getKey(), entry.getValue());
        }
      }
      StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
      httpPost.setEntity(entity);

      response = httpclient.execute(httpPost);
      return getResponse(response, context, "POST");
    } catch (Exception ex) {
      logger.error(context, "Exception occurred while calling Post method", ex);
      return "";
    } finally {
      closeResponse(response, context, "POST");
    }
  }

  /**
   * Executes an HTTP POST request with form-encoded data.
   *
   * @param requestURL the URL to send the request to
   * @param params the form parameters to send
   * @param headers optional headers to include in the request
   * @param context the request context for logging
   * @return the response body as a string, or empty string on error
   */
  public static String postFormData(
      String requestURL,
      Map<String, String> params,
      Map<String, String> headers,
      RequestContext context) {
    CloseableHttpResponse response = null;
    try {
      HttpPost httpPost = new HttpPost(requestURL);
      if (MapUtils.isNotEmpty(headers)) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          httpPost.addHeader(entry.getKey(), entry.getValue());
        }
      }

      List<NameValuePair> form = new ArrayList<>();
      for (Map.Entry<String, String> entry : params.entrySet()) {
        form.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
      }
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);

      httpPost.setEntity(entity);

      response = httpclient.execute(httpPost);
      return getResponse(response, context, "postFormData");
    } catch (Exception ex) {
      logger.error(context, "Exception occurred while calling postFormData method", ex);
      return "";
    } finally {
      closeResponse(response, context, "postFormData");
    }
  }

  /**
   * Executes an HTTP PATCH request with JSON payload.
   *
   * @param requestURL the URL to send the request to
   * @param params the JSON string to send as request body
   * @param headers optional headers to include in the request
   * @param context the request context for logging
   * @return the response body as a string, or empty string on error
   */
  public static String patch(
      String requestURL, String params, Map<String, String> headers, RequestContext context) {
    CloseableHttpResponse response = null;
    try {
      HttpPatch httpPatch = new HttpPatch(requestURL);
      if (MapUtils.isNotEmpty(headers)) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          httpPatch.addHeader(entry.getKey(), entry.getValue());
        }
      }
      StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
      httpPatch.setEntity(entity);

      response = httpclient.execute(httpPatch);
      return getResponse(response, context, "PATCH");
    } catch (Exception ex) {
      logger.error(context, "Exception occurred while calling patch method", ex);
      return "";
    } finally {
      closeResponse(response, context, "PATCH");
    }
  }

  /**
   * Executes an HTTP DELETE request.
   *
   * @param requestURL the URL to send the request to
   * @param headers optional headers to include in the request
   * @param context the request context for logging
   * @return the response body as a string, or empty string on error
   */
  public static String delete(
      String requestURL, Map<String, String> headers, RequestContext context) {
    CloseableHttpResponse response = null;
    try {
      HttpDelete httpDelete = new HttpDelete(requestURL);
      if (MapUtils.isNotEmpty(headers)) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          httpDelete.addHeader(entry.getKey(), entry.getValue());
        }
      }
      response = httpclient.execute(httpDelete);
      return getResponse(response, context, "DELETE");
    } catch (Exception ex) {
      logger.error(context, "Exception occurred while calling delete method", ex);
      return "";
    } finally {
      closeResponse(response, context, "DELETE");
    }
  }

  /**
   * Extracts the response body from an HTTP response.
   *
   * @param response the HTTP response object
   * @param context the request context for logging
   * @param method the HTTP method name for logging
   * @return the response body as a string, or empty string on error
   * @throws IOException if an I/O error occurs
   */
  private static String getResponse(
      CloseableHttpResponse response, RequestContext context, String method) throws IOException {
    int status = response.getStatusLine().getStatusCode();
    if (status >= 200 && status < 300) {
      HttpEntity httpEntity = response.getEntity();
      StatusLine sl = response.getStatusLine();
      logger.debug(
          context,
          "Response from "
              + method
              + " call : "
              + sl.getStatusCode()
              + " - "
              + sl.getReasonPhrase());
      if (null != httpEntity) {
        byte[] bytes = EntityUtils.toByteArray(httpEntity);
        String resp = new String(bytes);
        logger.info(context, "Got response from " + method + " call : " + resp);
        return resp;
      } else {
        return "";
      }
    } else {
      getErrorResponse(response, method, context);
      return "";
    }
  }

  /**
   * Logs error response details.
   *
   * @param response the HTTP response object
   * @param method the HTTP method name for logging
   * @param context the request context for logging
   */
  private static void getErrorResponse(
      CloseableHttpResponse response, String method, RequestContext context) {
    try {
      HttpEntity httpEntity = response.getEntity();
      byte[] bytes = EntityUtils.toByteArray(httpEntity);
      StatusLine sl = response.getStatusLine();
      String resp = new String(bytes);
      logger.info(
          context,
          "Response from : "
              + method
              + " call "
              + resp
              + " status "
              + sl.getStatusCode()
              + " - "
              + sl.getReasonPhrase());
    } catch (Exception ex) {
      logger.error(context, "Exception occurred while fetching response for method " + method, ex);
    }
  }

  /**
   * Closes the HTTP response object safely.
   *
   * @param response the HTTP response object to close
   * @param context the request context for logging
   * @param method the HTTP method name for logging
   */
  private static void closeResponse(
      CloseableHttpResponse response, RequestContext context, String method) {
    if (null != response) {
      try {
        response.close();
      } catch (Exception ex) {
        logger.error(
            context, "Exception occurred while closing " + method + " response object", ex);
      }
    }
  }
}
