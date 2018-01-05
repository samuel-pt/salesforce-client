package com.palmtree.salesforce.client.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to access Salesforce Rest API
 */
public class HttpHelper {
  private static final Logger LOG = Logger.getLogger(HttpHelper.class.getName());

  private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
  private static final String HEADER_AUTH = "Authorization";
  private static final String HEADER_OAUTH = "OAuth ";
  private static final String HEADER_ACCEPT = "Accept";

  public String post(String uri, String sessionId, String request) throws Exception {
    return post(uri, sessionId, request, CONTENT_TYPE_APPLICATION_JSON);
  }

  public String post(String uri, List<NameValuePair> nameValuePairs) throws Exception {

    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairs);
    HttpPost httpPost = new HttpPost(uri);
    httpPost.setEntity(formEntity);

    return execute(uri, httpPost);
  }

  public String post(String uri, String sessionId, String request,
                     String contentType) throws Exception {

    LOG.info("Executing POST request on " + uri);
    System.out.println("Executing POST request on " + uri);
    LOG.info("Sending request " + request);
    LOG.info("Content-Type " + contentType);

    StringEntity entity = new StringEntity(request, Charset.defaultCharset());
    if (contentType != null) {
      entity.setContentType(contentType);
    } else {
      LOG.info("As Content-Type is null " + CONTENT_TYPE_APPLICATION_JSON + " Content-Type is " +
                   "used");
      entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);
    }

    HttpPost httpPost = new HttpPost(uri);
    httpPost.setEntity(entity);
    httpPost.addHeader(HEADER_AUTH, HEADER_OAUTH + sessionId);

    return execute(uri, httpPost);
  }

  public String get(String uri, String sessionId) throws Exception {
    LOG.info("Executing GET request on " + uri);
    System.out.println("Executing GET request on " + uri);
    HttpGet httpGet = new HttpGet(uri);
    httpGet.addHeader(HEADER_AUTH, HEADER_OAUTH + sessionId);
    httpGet.addHeader(HEADER_ACCEPT, CONTENT_TYPE_APPLICATION_JSON);

    return execute(uri, httpGet);
  }

  private String execute(String uri, HttpUriRequest httpReq) throws Exception {
    CloseableHttpClient httpClient = HttpClients.createDefault();

    InputStream eis = null;
    try {
      CloseableHttpResponse response = httpClient.execute(httpReq);

      int statusCode = response.getStatusLine().getStatusCode();
      if (!(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED)) {
        String reasonPhrase = response.getStatusLine().getReasonPhrase();
        String errResponse = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        throw new Exception(
                               String.format("Accessing %s failed. Status %d. Reason %s \n " +
                                                 "Error from server %s", uri, statusCode,
                                   reasonPhrase, errResponse));
      }

      HttpEntity responseEntity = response.getEntity();
      eis = responseEntity.getContent();
      return IOUtils.toString(eis, Charset.defaultCharset());
    } finally {
      try {
        if (httpClient != null) {
          httpClient.close();
        }
      } catch (Exception e) {
        LOG.log(Level.FINE, "Error while closing HTTP Client", e);
      }

      try {
        if (eis != null) {
          eis.close();
        }
      } catch (Exception e) {
        LOG.log(Level.FINE, "Error while closing InputStream", e);
      }
    }
  }
}
