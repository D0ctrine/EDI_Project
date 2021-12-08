package com.edi.domain.application.commands.setting.fileDefine;

import com.edi.domain.model.user.UserId;

public class UpdateFileDefCommand {
  private Long id;
  private UserId userid;
  private String cfgId;
  private String fileName;
  private String description;
  private String fileSaveFolder;
  private String fileCharset;
  private String dataType;
  private String fileType;
  private String ftpEnvid;
  private String schMin;
  private String schHour;
  private String schDay;
  private String schWeek;
  private String schMonth;
  private String noDataSend;

  public UpdateFileDefCommand(Long id,UserId userId, String cfgId, String fileName, String description,String fileSaveFolder,String fileCharset, String dataType
  ,String fileType, String ftpEnvid, String schMin, String schHour, String schDay, String schWeek, String schMonth,String noDataSend){
    this.id = id;
    this.userid = userId;
    this.cfgId = cfgId;
    this.fileName = fileName;
    this.description = description;
    this.fileSaveFolder = fileSaveFolder;
    this.fileCharset = fileCharset;
    this.dataType = dataType;
    this.fileType = fileType;
    this.ftpEnvid = ftpEnvid;
    this.schMin = schMin;
    this.schHour = schHour;
    this.schDay = schDay;
    this.schWeek = schWeek;
    this.schMonth = schMonth;
    this.noDataSend = noDataSend;
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

  public String getFileName() {
    return fileName;
  }

  public String getDescription() {
    return description;
  }

  public String getFileCharset() {
    return fileCharset;
  }

  public String getDataType() {
    return dataType;
  }

  public String getFileType() {
    return fileType;
  }

  public String getFtpEnvid() {
    return ftpEnvid;
  }

  public String getSchMin() {
    return schMin;
  }

  public String getSchHour() {
    return schHour;
  }

  public String getSchDay() {
    return schDay;
  }

  public String getSchWeek() {
    return schWeek;
  }

  public String getSchMonth() {
    return schMonth;
  }

  public String getNoDataSend() {
    return noDataSend;
  }

  public String getFileSaveFolder() {
    return fileSaveFolder;
  }

}
