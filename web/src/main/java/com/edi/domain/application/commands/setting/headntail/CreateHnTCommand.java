package com.edi.domain.application.commands.setting.headntail;

import com.edi.domain.model.user.UserId;

public class CreateHnTCommand {
  private UserId userId;
  private String cfgId;
  private String dataType;
  private String note;
  private String value;

  public CreateHnTCommand(UserId userId, String cfgId, String dataType, String note, String value){
    this.userId = userId;
    this.cfgId = cfgId;
    this.dataType = dataType;
    this.note = note;
    this.value = value;
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
