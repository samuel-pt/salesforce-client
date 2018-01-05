package com.palmtree.salesforce.client.model;

/**
 * Created by sam on 13/9/17.
 */
public class ReportRunAsyncResponse {
  private Object completionDate;
  private Boolean hasDetailRows;
  private String id;
  private String ownerId;
  private Boolean queryable;
  private String requestDate;
  private String status;
  private String url;

  public Object getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(Object completionDate) {
    this.completionDate = completionDate;
  }

  public Boolean getHasDetailRows() {
    return hasDetailRows;
  }

  public void setHasDetailRows(Boolean hasDetailRows) {
    this.hasDetailRows = hasDetailRows;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public Boolean getQueryable() {
    return queryable;
  }

  public void setQueryable(Boolean queryable) {
    this.queryable = queryable;
  }

  public String getRequestDate() {
    return requestDate;
  }

  public void setRequestDate(String requestDate) {
    this.requestDate = requestDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}