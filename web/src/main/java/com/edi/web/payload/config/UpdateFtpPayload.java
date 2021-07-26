package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.ftp.UpdateFtpCommand;
import com.edi.domain.model.user.UserId;

public class UpdateFtpPayload {
  private Long id;
  private String cfgId;
  private String ftp_addr;
  private String user_id;
  private String pwd;
  private String base_dir;
  private String type;

  public UpdateFtpCommand toCommand(UserId userId){
    return new UpdateFtpCommand(id, userId, cfgId, ftp_addr, user_id, pwd, base_dir, type);
  }
  public Long getId() {
    return id;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getFtp_addr() {
    return ftp_addr;
  }

  public String getUser_id() {
    return user_id;
  }

  public String getPwd() {
    return pwd;
  }

  public String getBase_dir() {
    return base_dir;
  }

  public String getType() {
    return type;
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }
}
