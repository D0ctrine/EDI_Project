package com.edi.domain.application.commands.setting.fileDefine;

import com.edi.domain.model.user.UserId;

public class UpdateFileDefCommand {
  private Long id;
  private UserId userid;
  private String cfgId;
  private String description;
  private String cronData;
  private String fileExtractType;

  public UpdateFileDefCommand(Long id,UserId userId,String cfgId,String description,String cronData,String fileExtractType){
    this.id = id;
    this.userid = userId;
    this.cfgId = cfgId;
    this.description = description;
    this.cronData = cronData;
    this.fileExtractType = fileExtractType;
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
