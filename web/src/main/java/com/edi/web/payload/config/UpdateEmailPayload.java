package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.email.UpdateEmailCommand;
import com.edi.domain.model.user.UserId;

public class UpdateEmailPayload {
  private Long id;
  private String cfgId;
  private String host;
  private String port;
  private String access_id;
  private String access_pwd;

  public UpdateEmailCommand toCommand(UserId userId){
    return new UpdateEmailCommand(id,userId,cfgId,host,port,access_id,access_pwd);
}
  public Long getId() {
    return id;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public String getAccess_id() {
    return access_id;
  }

  public String getAccess_pwd() {
    return access_pwd;
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }
}
