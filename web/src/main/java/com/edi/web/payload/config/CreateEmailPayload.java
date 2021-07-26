package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.email.CreateEmailCommand;
import com.edi.domain.model.user.UserId;

public class CreateEmailPayload {
  private String cfgId;
  private String host;
  private String port;
  private String access_id;
  private String access_pwd;

  public CreateEmailCommand toCommand(UserId userId) {
    return new CreateEmailCommand(userId,cfgId,host,port,access_id,access_pwd);
  }
  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public void setAccess_id(String access_id) {
    this.access_id = access_id;
  }

  public void setAccess_pwd(String access_pwd) {
    this.access_pwd = access_pwd;
  }

}
