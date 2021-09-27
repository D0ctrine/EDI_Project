package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.query.CreateQueryCommand;
import com.edi.domain.model.user.UserId;

public class CreateQueryPayload {
  private String settingId;
  private String key;
  private String type;
  private String query;
  private String dbtype;

  public CreateQueryCommand toCommand(UserId userId) {
    return new CreateQueryCommand(userId,settingId,key,type,query,dbtype);
  }

  public void setSettingId(String settingId) {
    this.settingId = settingId;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public void setType(String type) {
    this.type = type;
  }
  public void setQuery(String query) {
    this.query = query;
  }
  public void setDbtype(String dbtype) {
    this.dbtype = dbtype;
  }
}

