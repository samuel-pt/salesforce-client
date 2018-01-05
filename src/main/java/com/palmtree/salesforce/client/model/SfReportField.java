package com.palmtree.salesforce.client.model;

/**
 * Created by sam on 12/9/17.
 */
public class SfReportField {
  private String name;
  private String label;
  private String type;

  public SfReportField(String name, String label, String type) {
    this.name = name;
    this.label = label;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return "SfReportField{" +
               "name='" + name + '\'' +
               ", label='" + label + '\'' +
               ", type='" + type + '\'' +
               '}';
  }
}
