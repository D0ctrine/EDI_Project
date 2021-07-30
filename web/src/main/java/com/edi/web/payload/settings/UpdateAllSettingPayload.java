package com.edi.web.payload.settings;

import java.util.List;

import com.edi.web.payload.config.UpdateEnvPayload;
import com.edi.web.payload.config.UpdateQueryPayload;

public class UpdateAllSettingPayload {
  private List<UpdateEnvPayload> env;
  private List<UpdateQueryPayload> itemGrp;
  private UpdateFileDefPayload fileDef;
  private List<UpdateHnTPayload> headNtail;
  private String cg_id;

  public List<UpdateEnvPayload> getEnv() {
    return env;
  }
  public void setEnv(List<UpdateEnvPayload> env) {
    this.env = env;
  }
  public List<UpdateQueryPayload> getItemGrp() {
    return itemGrp;
  }
  public void setItemGrp(List<UpdateQueryPayload> itemGrp) {
    this.itemGrp = itemGrp;
  }
  public UpdateFileDefPayload getFileDef() {
    return fileDef;
  }
  public void setFileDef(UpdateFileDefPayload fileDef) {
    this.fileDef = fileDef;
  }
  public List<UpdateHnTPayload> getHeadNtail() {
    return headNtail;
  }
  public void setHeadNtail(List<UpdateHnTPayload> headNtail) {
    this.headNtail = headNtail;
  }
  public String getCg_id() {
    return cg_id;
  }
  public void setCg_id(String cg_id) {
    this.cg_id = cg_id;
  }

}
