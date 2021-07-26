package com.edi.domain.application.commands.setting.headntail;

import com.edi.domain.model.user.UserId;

public class CreateHnTCommand {
  private UserId userId;
  private String cfgId;
  private String dataType;
  private String orderNumber;
  private String value;

  public CreateHnTCommand(UserId userId, String cfgId, String dataType, String orderNumber, String value){
    this.userId = userId;
    this.cfgId = cfgId;
    this.dataType = dataType;
    this.orderNumber = orderNumber;
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

  public String getOrderNumber() {
    return orderNumber;
  }

  public String getValue() {
    return value;
  }

}
