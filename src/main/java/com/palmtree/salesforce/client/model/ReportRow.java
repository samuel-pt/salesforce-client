package com.palmtree.salesforce.client.model;

import java.util.List;

/**
 * Created by sam on 12/9/17.
 */
public class ReportRow {
  private List<Object> data;

  public ReportRow(List<Object> data) {
    this.data = data;
  }

  public Object get(int i) {
    return data.get(i);
  }

  public String getAsString(int i) {
    return String.valueOf(data.get(i));
  }

  public Boolean getAsBoolean(int i) {
    return Boolean.parseBoolean(getAsString(i));
  }

  public Integer getAsInteger(int i) {
    return Integer.parseInt(getAsString(i));
  }

  public Double getAsDouble(int i) {
    return Double.parseDouble(getAsString(i));
  }

  @Override
  public String toString() {
    return "ReportRow{" +
               "data=" + data +
               '}';
  }
}
