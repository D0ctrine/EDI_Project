package com.edi.domain.application.commands.config.query;

import com.edi.domain.model.user.UserId;

public class UpdateQueryCommand {
  private Long id;
  private UserId userId;
  private String settingId;
  private String key;
  private String type;
  private String query;

  public UpdateQueryCommand(Long id,UserId userId,String settingId,String key,String type,String query){
    this.id = id;
    this.userId = userId;
    this.settingId = settingId;
    this.key = key;
    this.type = type;
    this.query = query;
  }

  public Long getId() {
    return id;
  }

  public UserId getUserId() {
    return userId;
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
}
