package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.headntail.UpdateHnTCommand;
import com.edi.domain.model.user.UserId;

public class UpdateHnTPayload {
  private Long id;
  private String cfgId;
  private String data_type;
  private String note;
  private String value;

  public UpdateHnTCommand toCommand(UserId userId){
    return new UpdateHnTCommand(id, userId, cfgId, data_type, note, value);
  }

  public Long getId() {
    return id;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getData_type() {
    return data_type;
  }

  public String getNote() {
    return note;
  }

  public String getValue() {
    return value;
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

}
