package com.edi.domain.application.commands.setting.fileDefine;

import com.edi.domain.model.user.UserId;

public class CreateFileDefCommand {
  private UserId userId;
  private String cfgId;
  private String fileName;
  private String description;
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
  private String sendFlag;

public CreateFileDefCommand(UserId userId, String cfgId, String fileName, String description, String fileCharset, String dataType
                            ,String fileType, String ftpEnvid, String schMin, String schHour, String schDay, String schWeek, String schMonth,String noDataSend,String sendFlag){
  this.userId = userId;
  this.cfgId = cfgId;
  this.fileName = fileName;
  this.description = description;
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
  this.sendFlag = sendFlag;
}

public UserId getUserId() {
  return userId;
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

public String getSendFlag() {
  return sendFlag;
}

}
