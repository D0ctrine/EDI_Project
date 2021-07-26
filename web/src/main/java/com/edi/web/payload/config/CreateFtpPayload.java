package com.edi.web.payload.config;

import com.edi.domain.application.commands.config.ftp.CreateFtpCommand;
import com.edi.domain.model.user.UserId;

public class CreateFtpPayload {
  private String cfgId;
  private String ftp_addr;
  private String user_id;
  private String pwd;
  private String base_dir;
  private String type;

  public CreateFtpCommand toCommand(UserId userId) {
    return new CreateFtpCommand(userId,cfgId,ftp_addr,user_id,pwd,base_dir,type);
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

  public void setFtp_addr(String ftp_addr) {
    this.ftp_addr = ftp_addr;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public void setPwd(String pwd) {
    this.pwd = pwd;
  }

  public void setBase_dir(String base_dir) {
    this.base_dir = base_dir;
  }

  public void setType(String type) {
    this.type = type;
  }


}
