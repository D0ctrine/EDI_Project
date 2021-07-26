package com.edi.domain.application.commands.config.environment;

import com.edi.domain.model.user.UserId;

public class UpdateEnvCommand {
  private Long id;
  private UserId userid;
  private String cfgId;
  private String item;
  private String value;
  private String note;
  private String set_type;

  public UpdateEnvCommand(Long id,UserId userId,String cfgId,String item,String value,String note,String set_type){
    this.id = id;
    this.userid = userId;
    this.cfgId = cfgId;
    this.item = item;
    this.value = value;
    this.note = note;
    this.set_type = set_type;
  }

  public Long getId() {
    return id;
  }
  public UserId getUserid() {
    return userid;
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
  public void setSet_type(String set_type) {
    this.set_type = set_type;
  }
}
