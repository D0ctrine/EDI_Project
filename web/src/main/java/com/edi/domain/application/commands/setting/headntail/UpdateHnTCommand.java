package com.edi.domain.application.commands.setting.headntail;

import com.edi.domain.model.user.UserId;

public class UpdateHnTCommand {
  private Long id;
  private UserId userId;
  private String cfgId;
  private String dataType;
  private String note;
  private String value;

  public UpdateHnTCommand(Long id, UserId userId, String cfgId, String dataType, String note, String value){
    this.id = id;
    this.userId = userId;
    this.cfgId = cfgId;
    this.dataType = dataType;
    this.note = note;
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  public UserId getUserId() {
    return userId;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getDataType() {
    return dataType;
  }

  public String getNote() {
    return note;
  }

  public String getValue() {
    return value;
  }

}
