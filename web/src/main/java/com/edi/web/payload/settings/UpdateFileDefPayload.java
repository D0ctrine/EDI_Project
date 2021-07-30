package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.model.user.UserId;

public class UpdateFileDefPayload {
  private Long id;
  private String cfgId;
  private String fileDesc;
  private String cronData;
  private String extractType;

  public UpdateFileDefCommand toCommand(UserId userId){
    return new UpdateFileDefCommand(id, userId, cfgId, fileDesc, cronData, extractType);
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

  public String getCronData() {
    return cronData;
  }

  public String getExtractType() {
    return extractType;
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

}
