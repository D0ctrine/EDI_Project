package com.edi.domain.application.commands.setting.fileDefine;

import com.edi.domain.model.user.UserId;

public class CreateFileDefCommand {
  private UserId userId;
  private String cfgId;
  private String description;
  private String cronData;
  private String fileExtractType;

public CreateFileDefCommand(UserId userId,String cfgId,String description,String cronData,String fileExtractType){
  this.userId = userId;
  this.cfgId = cfgId;
  this.description = description;
  this.cronData = cronData;
  this.fileExtractType = fileExtractType;
}

public UserId getUserId() {
  return userId;
}

public String getCfgId() {
  return cfgId;
}

public String getDescription() {
  return description;
}

public String getCronData() {
  return cronData;
}

public String getFileExtractType() {
  return fileExtractType;
}

}
