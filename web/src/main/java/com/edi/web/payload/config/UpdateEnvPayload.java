package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.environment.UpdateEnvCommand;
import com.edi.domain.model.user.UserId;

public class UpdateEnvPayload {
  private Long id;
  private String cfgId;
  private String item;
  private String value;
  private String note;
  private String set_type;

  public UpdateEnvCommand toCommand(UserId userId) {
    return new UpdateEnvCommand(id, userId, cfgId, item, value, note,set_type);
  }
  public Long getId() {
    return id;
  }
  public String getCfgId() {
    return cfgId;
  }
  public String getItem() {
    return item;
  }
  public String getValue() {
    return value;
  }
  public String getNote() {
    return note;
  }
  public String getSet_type() {
    return set_type;
  }
  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

}
