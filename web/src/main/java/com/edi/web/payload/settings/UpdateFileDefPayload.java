package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.model.user.UserId;

public class UpdateFileDefPayload {
  private Long id;
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


  public UpdateFileDefCommand toCommand(UserId userId){
    return new UpdateFileDefCommand(id, userId, cfgId, fileName, fileDesc, fileSaveFolder, fileCharset, dataType, fileType, ftpEnvid, schMin, schHour, schDay, schWeek, schMonth, noDataSend);
  }

  public Long getId() {
    return id;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getFileDesc() {
    return fileDesc;
  }


  public String getFileName() {
    return fileName;
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

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

}
