package com.edi.domain.application.commands.config.ftp;

import com.edi.domain.model.user.UserId;

public class UpdateFtpCommand {
  private Long id;
  private UserId userId;
  private String cfgId;
  private String ftpAddr;
  private String userid;
  private String pwd;
  private String baseDir;
  private String type;

  public UpdateFtpCommand(Long id,UserId userId,String cfgId,String ftpAddr,String userid,String pwd,String baseDir,String type){
    this.id = id;
    this.userId = userId;
    this.cfgId = cfgId;
    this.ftpAddr = ftpAddr;
    this.userid = userid;
    this.pwd = pwd;
    this.baseDir = baseDir;
    this.type = type;
  }

  public Long getId() {
    return id;
  }

  public UserId getUserId() {
    return userId;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getFtpAddr() {
    return ftpAddr;
  }

  public String getUserid() {
    return userid;
  }

  public String getPwd() {
    return pwd;
  }

  public String getBaseDir() {
    return baseDir;
  }

  public String getType() {
    return type;
  }
}
