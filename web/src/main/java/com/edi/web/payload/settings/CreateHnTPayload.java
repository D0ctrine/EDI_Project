package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.headntail.CreateHnTCommand;
import com.edi.domain.model.user.UserId;

public class CreateHnTPayload {
  private String cfgId;
  private String dataType;
  private String orderNumber;
  private String value;

  public CreateHnTCommand toCommand(UserId userId) {
    return new CreateHnTCommand(userId, cfgId, dataType, orderNumber, value);
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
