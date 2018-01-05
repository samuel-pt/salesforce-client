package com.palmtree.salesforce.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sam on 12/9/17.
 */
public class ReportData {
  private static final String FIELDS_PATH = "reportExtendedMetadata.detailColumnInfo";
  private static final String DATA_PATH = "factMap.T!T";

  private static final String DATA_TYPE = "dataType";
  private static final String LABEL = "label";
  private static final String VALUE = "value";
  private static final String DATA_CELLS = "dataCells";

  private List<SfReportField> fields;
  private List<ReportRow> rows;

  public ReportData(String json) {
    parseData(json);
  }

  public ReportData(JsonObject jsonObject) {
    parseData(jsonObject);
  }

  private void parseData(String json) {
    Gson gson = new GsonBuilder().create();
    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
    parseData(jsonObject);
  }

  private void parseData(JsonObject jsonObject) {
    initFields(jsonObject);
    initRows(jsonObject);

    System.out.println(fields.size());
    System.out.println(rows.size());
  }

  private void initRows(JsonObject jsonObject) {
    JsonObject dataJsonObject = getJsonObjectBasedOnPath(jsonObject, DATA_PATH);
    if (dataJsonObject == null) {
      // No rows present in result
      return;
    }

    JsonElement rowsJsonElement = dataJsonObject.get("rows");
    if (rowsJsonElement == null || !rowsJsonElement.isJsonArray()) {
      // No rows present in result
      return;
    }

    JsonArray jsonArray = rowsJsonElement.getAsJsonArray();
    int size = jsonArray.size();
    rows = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      JsonElement jsonElement = jsonArray.get(i);
      JsonElement dataCellsElem = jsonElement.getAsJsonObject().get(DATA_CELLS);
      if (dataCellsElem != null && dataCellsElem.isJsonArray()) {
        rows.add(getAsReportRow(dataCellsElem.getAsJsonArray()));
      }
    }
  }

  private ReportRow getAsReportRow(JsonArray dataCells) {
    int size = dataCells.size();
    List<Object> row = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      JsonObject jsonField = dataCells.get(i).getAsJsonObject();
      JsonElement valueElement = jsonField.get(VALUE);
      if (valueElement.isJsonNull()) {
        row.add(null);
      } else {
        row.add(valueElement.toString());
      }
    }

    return new ReportRow(row);
  }

  private void initFields(JsonObject jsonObject) {
    JsonObject fieldJsonObject = getJsonObjectBasedOnPath(jsonObject, FIELDS_PATH);
    if (fieldJsonObject == null) {
      // Response does not contain field details
      return;
    }

    fields = new ArrayList<>();
    Set<Map.Entry<String, JsonElement>> entries = fieldJsonObject.entrySet();
    for (Map.Entry<String, JsonElement> entry : entries) {
      String fieldName = entry.getKey();
      JsonObject fieldMetadataJson = entry.getValue().getAsJsonObject();
      String dataType = fieldMetadataJson.get(DATA_TYPE).getAsString();
      String label = fieldMetadataJson.get(LABEL).getAsString();
      fields.add(new SfReportField(fieldName, label, dataType));
    }
  }

  private JsonObject getJsonObjectBasedOnPath(JsonObject jsonObject, String path) {
    String[] elements = path.split("\\.");
    for (String element : elements) {
      JsonElement jsonElement = jsonObject.get(element);
      if (jsonElement.isJsonObject()) {
        jsonObject = jsonElement.getAsJsonObject();
      } else {
        // required path not found
        return null;
      }
    }

    return jsonObject;
  }

  public List<SfReportField> getFields() {
    return fields;
  }

  public void setFields(List<SfReportField> fields) {
    this.fields = fields;
  }

  public List<ReportRow> getRows() {
    return rows;
  }

  public void setRows(List<ReportRow> rows) {
    this.rows = rows;
  }

  @Override
  public String toString() {
    return "ReportData{" +
               "fields=" + fields +
               ", rows=" + rows +
               '}';
  }
}
