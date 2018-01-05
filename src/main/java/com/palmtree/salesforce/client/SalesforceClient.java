package com.palmtree.salesforce.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.palmtree.salesforce.client.model.LoginResponse;
import com.palmtree.salesforce.client.model.Report;
import com.palmtree.salesforce.client.model.ReportData;
import com.palmtree.salesforce.client.model.ReportRunAsyncResponse;
import com.palmtree.salesforce.client.util.HttpHelper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * API for accessing Salesforce via Rest API
 */
public class SalesforceClient {
  private static final Logger LOG = Logger.getLogger(SalesforceClient.class.getName());

  private static final String OAUTH_TOKEN_PATH = "/services/oauth2/token";
  private static final String REVOKE_OAUTH_TOKEN_PATH = "/services/oauth2/revoke";
  private static final String SERVICE_PATH = "/services/data/";
  private static final String REPORTS_PATH = "/analytics/reports/";
  private static final String QUERY_PATH = "/query";
  private static final String DESCRIBE_PATH = "/describe";
  private static final String INSTANCES_PATH = "/instances/";

  private static final String TABULAR_REPORTS_SOQL = "select Id, Name from Report where Format=" +
                                                         "'TABULAR'";

  private static final String CLIENT_ID_KEY = "client_id";
  private static final String CLIENT_SECRET_KEY = "client_secret";
  private static final String GRANT_TYPE_KEY = "grant_type";
  private static final String USERNAME_KEY = "username";
  private static final String PASSWORD_KEY = "password";
  private static final String ATTRIBUTES = "attributes";
  private static final String STATUS = "status";
  private static final String STATUS_NEW = "New";
  private static final String STATUS_RUNNING = "Running";
  private static final String STATUS_ERROR = "Error";
  private static final String SOQL_QUERY_PARAM = "q=";
  private static final String RECORDS = "records";
  private static final String ID = "Id";
  private static final String NAME = "Name";

  private String loginUrl;
  private String userId;
  private String password;
  private String clientId;
  private String clientSecret;
  private String apiVersion;
  private LoginResponse loginResponse;
  private HttpHelper httpHelper;

  public SalesforceClient(String loginUrl, String userId, String password, String clientId,
                          String clientSecret) throws Exception {
    this(loginUrl, userId, password, clientId, clientSecret, "40.0");
  }

  public SalesforceClient(String loginUrl, String userId, String password,
                          String clientId, String clientSecret, String apiVersion) throws
      Exception {
    this.loginUrl = loginUrl;
    this.userId = userId;
    this.password = password;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.apiVersion = apiVersion;
    this.httpHelper = new HttpHelper();
  }

  /**
   * Creates a new Salesforce session
   * @return
   * @throws Exception
   */
  public LoginResponse login() throws Exception {
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    nameValuePairs.add(new BasicNameValuePair(CLIENT_ID_KEY, clientId));
    nameValuePairs.add(new BasicNameValuePair(CLIENT_SECRET_KEY, clientSecret));
    nameValuePairs.add(new BasicNameValuePair(GRANT_TYPE_KEY, PASSWORD_KEY));
    nameValuePairs.add(new BasicNameValuePair(USERNAME_KEY, userId));
    nameValuePairs.add(new BasicNameValuePair(PASSWORD_KEY, password));

    String response = httpHelper.post(loginUrl + OAUTH_TOKEN_PATH, nameValuePairs);

    System.out.println(response);
    loginResponse = new Gson().fromJson(response, LoginResponse.class);
    // TODO : Validate response
    return loginResponse;
  }

  /**
   * User to be logged out of a Salesforce Session
   * @throws Exception
   */
  public void logout() throws Exception {
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    nameValuePairs.add(new BasicNameValuePair("token", getAccessToken()));
    String response = httpHelper.post(getInstanceUrl() + REVOKE_OAUTH_TOKEN_PATH, nameValuePairs);

    System.out.println(response);
  }

  /**
   * Fetch all reports present in Salesforce account
   * @return
   * @throws Exception
   */
  public List<Report> getAllReports() throws Exception {
    String reportsUri = getRequestURI(getReportsPath());
    String responseContent = httpHelper.get(reportsUri, getAccessToken());

    Gson gson = new GsonBuilder().create();
    Type listType = new TypeToken<List<Report>>() {}.getType();

    return gson.fromJson(responseContent, listType);
  }

  /**
   * Fetch all reports present in Salesforce account
   * @return
   * @throws Exception
   */
  public List<Report> getTabularReports() throws Exception {
    String queryUri = getRequestURI(getQueryPath(), SOQL_QUERY_PARAM + TABULAR_REPORTS_SOQL);
    String sfSoqlQueryRespStr = httpHelper.get(queryUri, getAccessToken());

    Gson gson = new GsonBuilder().create();
    JsonObject sfSoqlQueryResp = gson.fromJson(sfSoqlQueryRespStr, JsonObject.class);
    JsonElement recordsElem = sfSoqlQueryResp.get(RECORDS);

    List<Report> tabularReports = new ArrayList<>();
    if (recordsElem != null && recordsElem.isJsonArray()) {
      JsonArray recordsArr = recordsElem.getAsJsonArray();
      for (int i = 0, size = recordsArr.size(); i < size; i++) {
        JsonObject record = recordsArr.get(i).getAsJsonObject();
        String id = record.get(ID).getAsString();
        String name = record.get(NAME).getAsString();

        tabularReports.add(new Report(id, name));
      }
    }

    return tabularReports;
  }

  /**
   * Execute the report in sync and return the response after parsing
   * @param reportId
   * @return
   * @throws Exception
   */
  public ReportData runSyncReport(String reportId) throws Exception {
    String reportsUri = getRequestURI(getReportsPath(reportId));

    String reportResponse = httpHelper.get(reportsUri, getAccessToken());

    return new ReportData(reportResponse);
  }

  /**
   * Runs the report in Async, Wait until the execution completed in Salesforce
   * And then return the response
   * @param reportId
   * @return
   * @throws Exception
   */
  public ReportData runASyncReport(String reportId) throws Exception {
    String reportsDescribeUri = getRequestURI(getReportsDescribePath(reportId));
    String reportDesc = httpHelper.get(reportsDescribeUri, getAccessToken());

    String runAsyncReportUri = getRequestURI(getReportsInstancesPath(reportId));
    String asyncResponseStr = httpHelper.post(runAsyncReportUri, getAccessToken(), reportDesc);

    Gson gson = new GsonBuilder().create();
    ReportRunAsyncResponse asyncResponse = gson.fromJson(asyncResponseStr, ReportRunAsyncResponse.class);
    // TODO : Write code to fetch further if the result is truncated to 2000 rows
    JsonObject reportDataJson = waitAndFetchReportData(reportId, asyncResponse.getId(), 1);

    return new ReportData(reportDataJson);
  }

  private JsonObject waitAndFetchReportData(String reportId, String instanceId,
                                            int count) throws Exception {
    // TODO : Add log info
    System.out.println("Waiting until the report execution completed...");
    // Waiting till the report executed
    // Increasing the waiting period to avoid frequent calls to Salesforce
    Thread.sleep(1000 * count);

    String runAsyncReportUri = getRequestURI(getReportsInstancesPath(reportId, instanceId));
    String reportInstanceResponseStr = httpHelper.get(runAsyncReportUri, getAccessToken());
    writeToFile(reportId, reportInstanceResponseStr);
    System.out.println(reportInstanceResponseStr);
    Gson gson = new GsonBuilder().create();
    JsonObject reportInstanceResponseJsonObj = gson.fromJson(reportInstanceResponseStr, JsonObject.class);

    String status = getReportExecutionStatus(reportInstanceResponseJsonObj);
    if (STATUS_NEW.equals(status) || STATUS_RUNNING.equals(status)) {
      return waitAndFetchReportData(reportId, instanceId, count++);
    } else if (STATUS_ERROR.equals(status)) {
      // TODO : Log the response from Salesforce
      // TODO : throw ConnectorException
      throw new Exception("Error while running the report " + reportId);
    }

    return reportInstanceResponseJsonObj;
  }

  private void writeToFile(String reportId, String reportInstanceResponseStr) throws IOException {
    try (FileOutputStream fos = new FileOutputStream
                                ("/home/sam/work/projects/p/salesforce-client/aysnc_responce/" + reportId
                                                                 + "" +
                                                                 ".json");) {
      fos.write(reportInstanceResponseStr.getBytes());
    }
  }

  private String getReportExecutionStatus(JsonObject reportInstanceResponseJsonObj) {
    String status = null;
    JsonElement attrJsonElem = reportInstanceResponseJsonObj.get(ATTRIBUTES);
    if (attrJsonElem != null && attrJsonElem.isJsonObject()) {
      JsonElement statusJsonElem = attrJsonElem.getAsJsonObject().get(STATUS);
      if (statusJsonElem != null && statusJsonElem.isJsonPrimitive()) {
        status = statusJsonElem.getAsString();
      }
    }

    return status;
  }

  private String getAccessToken() throws Exception {
    if (loginResponse == null) {
      login();
    }

    return loginResponse.getAccessToken();
  }

  private String getInstanceUrl() throws Exception {
    if (loginResponse == null) {
      login();
    }

    return loginResponse.getInstanceUrl();
  }

  private String getQueryPath() {
    StringBuilder queryPath = new StringBuilder();
    queryPath.append(SERVICE_PATH);
    queryPath.append("v");
    queryPath.append(apiVersion);
    queryPath.append(QUERY_PATH);

    return queryPath.toString();
  }

  private String getReportsPath() {
    StringBuilder reportsPath = new StringBuilder();
    reportsPath.append(SERVICE_PATH);
    reportsPath.append("v");
    reportsPath.append(apiVersion);
    reportsPath.append(REPORTS_PATH);

    return reportsPath.toString();
  }

  private String getReportsPath(String reportId) {
    StringBuilder reportsPath = new StringBuilder();
    reportsPath.append(getReportsPath());
    reportsPath.append(reportId);

    return reportsPath.toString();
  }

  private String getReportsDescribePath(String reportId) {
    StringBuilder reportsDescribePath = new StringBuilder();
    reportsDescribePath.append(getReportsPath());
    reportsDescribePath.append(reportId);
    reportsDescribePath.append(DESCRIBE_PATH);

    return reportsDescribePath.toString();
  }

  private String getReportsInstancesPath(String reportId) {
    StringBuilder reportsInstancesPath = new StringBuilder();
    reportsInstancesPath.append(getReportsPath());
    reportsInstancesPath.append(reportId);
    reportsInstancesPath.append(INSTANCES_PATH);

    return reportsInstancesPath.toString();
  }

  private String getReportsInstancesPath(String reportId, String instanceId) {
    StringBuilder reportsInstancesPath = new StringBuilder();
    reportsInstancesPath.append(getReportsInstancesPath(reportId));
    reportsInstancesPath.append(instanceId);

    return reportsInstancesPath.toString();
  }

  public String getRequestURI(String path) throws Exception {
    URI seURI = new URI(getInstanceUrl());

    return new URI(seURI.getScheme(),seURI.getUserInfo(), seURI.getHost(), seURI.getPort(),
                      path, null, null).toString();
  }

  public String getRequestURI(String path, String query) throws Exception {
    URI seURI = new URI(getInstanceUrl());

    return new URI(seURI.getScheme(),seURI.getUserInfo(), seURI.getHost(), seURI.getPort(),
                      path, query, null).toString();
  }

}
