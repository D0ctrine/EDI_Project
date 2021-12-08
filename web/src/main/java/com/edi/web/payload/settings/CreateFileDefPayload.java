package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.model.user.UserId;

public class CreateFileDefPayload {
  private String cfgId;
  private String fileName;
  private String fileDesc;
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
  private String sendFlag;

  public CreateFileDefCommand toCommand(UserId userId) {
    return new CreateFileDefCommand(userId, cfgId, fileName, fileDesc, fileSaveFolder, fileCharset, dataType, fileType, ftpEnvid, schMin, schHour, schDay, schWeek, schMonth, noDataSend, sendFlag);
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setFileDesc(String fileDesc) {
    this.fileDesc = fileDesc;
  }

  public void setFileCharset(String fileCharset) {
    this.fileCharset = fileCharset;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public void setFtpEnvid(String ftpEnvid) {
    this.ftpEnvid = ftpEnvid;
  }

  public void setSchMin(String schMin) {
    this.schMin = schMin;
  }

  public void setSchHour(String schHour) {
    this.schHour = schHour;
  }

  public void setSchDay(String schDay) {
    this.schDay = schDay;
  }

  public void setSchWeek(String schWeek) {
    this.schWeek = schWeek;
  }

  public void setSchMonth(String schMonth) {
    this.schMonth = schMonth;
  }

  public void setNoDataSend(String noDataSend) {
    this.noDataSend = noDataSend;
  }

  public void setSendFlag(String sendFlag) {
    this.sendFlag = sendFlag;
  }

  public void setFileSaveFolder(String fileSaveFolder) {
    this.fileSaveFolder = fileSaveFolder;
  }

}
