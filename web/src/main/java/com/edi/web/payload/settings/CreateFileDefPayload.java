package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.model.user.UserId;

public class CreateFileDefPayload {
  private String cfgId;
  private String fileDesc;
  private String cronData;
  private String extractType;

  public CreateFileDefCommand toCommand(UserId userId) {
    return new CreateFileDefCommand(userId, cfgId, fileDesc, cronData, extractType);
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

  public void setFileDesc(String fileDesc) {
    this.fileDesc = fileDesc;
  }

  public void setCronData(String cronData) {
    this.cronData = cronData;
  }

  public void setExtractType(String extractType) {
    this.extractType = extractType;
  }

}
