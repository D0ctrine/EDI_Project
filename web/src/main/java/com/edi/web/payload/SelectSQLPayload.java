package com.edi.web.payload;

public class SelectSQLPayload {
  private String query;
  private String database;

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

}
