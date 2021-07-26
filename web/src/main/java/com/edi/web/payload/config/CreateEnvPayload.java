package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.environment.CreateEnvCommand;
import com.edi.domain.model.user.UserId;

public class CreateEnvPayload {
  private String cfgId;
  private String item;
  private String value;
  private String note;
  private String set_type;

  public CreateEnvCommand toCommand(UserId userId) {
    return new CreateEnvCommand(userId,cfgId,item,value,note,set_type);
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }
  public void setItem(String item) {
    this.item = item;
  }
  public void setValue(String value) {
    this.value = value;
  }
  public void setNote(String note) {
    this.note = note;
  }
  public void setSet_type(String set_type) {
    this.set_type = set_type;
  }

}
