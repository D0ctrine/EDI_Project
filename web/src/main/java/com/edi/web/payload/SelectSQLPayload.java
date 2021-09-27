package com.edi.web.payload;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectSQLPayload {
  private String query;
  private String database;
  private String configId;
  private ArrayList<HashMap<String,Object>> items;

  public String getQuery() {
    return query;
  }
  public void setQuery(String query) {
    this.query = query;
  }
  public String getDatabase() {
    return database;
  }
  public void setDatabase(String database) {
    this.database = database;
  }
  public String getConfigId() {
    return configId;
  }
  public void setConfigId(String configId) {
    this.configId = configId;
  }
  public ArrayList<HashMap<String,Object>> getItems() {
    return items;
  }
  public void setItems(ArrayList<HashMap<String,Object>> items) {
    this.items = items;
  }

}
