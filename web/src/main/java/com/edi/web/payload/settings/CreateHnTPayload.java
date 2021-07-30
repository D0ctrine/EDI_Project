package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.headntail.CreateHnTCommand;
import com.edi.domain.model.user.UserId;

public class CreateHnTPayload {
  private String cfgId;
  private String data_type;
  private String note;
  private String value;

  public CreateHnTCommand toCommand(UserId userId) {
    return new CreateHnTCommand(userId, cfgId, data_type, note, value);
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

  public void setData_type(String data_type) {
    this.data_type = data_type;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
