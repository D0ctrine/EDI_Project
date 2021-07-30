package com.edi.web.payload.settings;

import java.util.List;

import com.edi.web.payload.config.CreateEnvPayload;
import com.edi.web.payload.config.CreateQueryPayload;

public class CreateAllSettingPayload {
  private List<CreateEnvPayload> env;
  private List<CreateQueryPayload> itemGrp;
  private CreateFileDefPayload fileDef;
  private List<CreateHnTPayload> headNtail;
  private String cg_id;

  public List<CreateEnvPayload> getEnv() {
    return env;
  }
  public void setEnv(List<CreateEnvPayload> env) {
    this.env = env;
  }
  public List<CreateQueryPayload> getItemGrp() {
    return itemGrp;
  }
  public void setItemGrp(List<CreateQueryPayload> itemGrp) {
    this.itemGrp = itemGrp;
  }
  public CreateFileDefPayload getFileDef() {
    return fileDef;
  }
  public void setFileDef(CreateFileDefPayload fileDef) {
    this.fileDef = fileDef;
  }
  public List<CreateHnTPayload> getHeadNtail() {
    return headNtail;
  }
  public void setHeadNtail(List<CreateHnTPayload> headNtail) {
    this.headNtail = headNtail;
  }
  public String getCg_id() {
    return cg_id;
  }
  public void setCg_id(String cg_id) {
    this.cg_id = cg_id;
  }


}
