package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.headntail.UpdateHnTCommand;
import com.edi.domain.model.user.UserId;

public class UpdateHnTPayload {
  private Long id;
  private String cfgId;
  private String dataType;
  private String orderNumber;
  private String value;

  public UpdateHnTCommand toCommand(UserId userId){
    return new UpdateHnTCommand(id, userId, cfgId, dataType, orderNumber, value);
  }

  public Long getId() {
    return id;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getDataType() {
    return dataType;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public String getValue() {
    return value;
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

}
