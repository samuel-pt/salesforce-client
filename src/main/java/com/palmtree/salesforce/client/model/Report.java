package com.palmtree.salesforce.client.model;

/**
 * Model class for Salesforce Report object
 */
public class Report {

  private String describeUrl;
  private String id;
  private String instancesUrl;
  private String name;
  private String url;

  public Report() {}

  public Report(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getDescribeUrl() {
    return describeUrl;
  }

  public void setDescribeUrl(String describeUrl) {
    this.describeUrl = describeUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getInstancesUrl() {
    return instancesUrl;
  }

  public void setInstancesUrl(String instancesUrl) {
    this.instancesUrl = instancesUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "Report{" +
               "describeUrl='" + describeUrl + '\'' +
               ", id='" + id + '\'' +
               ", instancesUrl='" + instancesUrl + '\'' +
               ", name='" + name + '\'' +
               ", url='" + url + '\'' +
               '}';
  }
}
