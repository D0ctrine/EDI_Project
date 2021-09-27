package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.query.UpdateQueryCommand;
import com.edi.domain.model.user.UserId;

public class UpdateQueryPayload {
  private Long id;
  private String settingId;
  private String key;
  private String type;
  private String query;
  private String dbtype;

  public UpdateQueryCommand toCommand(UserId userId){
    return new UpdateQueryCommand(id, userId, settingId, key, type, query, dbtype);
  }

  public Long getId() {
    return id;
  }

  public String getSettingId() {
    return settingId;
  }

  public String getKey() {
    return key;
  }

  public String getType() {
    return type;
  }

  public String getQuery() {
    return query;
  }

  public void setSettingId(String settingId) {
    this.settingId = settingId;
  }

  public String getDbtype() {
    return dbtype;
  }
}
