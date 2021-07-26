package com.edi.web.payload.config;

import java.util.List;

public class CreateAllPayload {
  private List<CreateEnvPayload> env;
  private List<CreateFtpPayload> ftp;
  private List<CreateEmailPayload> email;
  private List<CreateQueryPayload> itemGrp;
  private String cg_id;

  @Override
  public String toString() {
    return "CreateAllPayload [cg_id=" + cg_id + ", email=" + email + ", env=" + env + ", ftp=" + ftp + ", itemGrp="
        + itemGrp + "]";
  }

  public List<CreateEnvPayload> getEnv() {
    return env;
  }
  public void setEnv(List<CreateEnvPayload> env) {
    this.env = env;
  }
  public List<CreateFtpPayload> getFtp() {
    return ftp;
  }
  public void setFtp(List<CreateFtpPayload> ftp) {
    this.ftp = ftp;
  }
  public List<CreateEmailPayload> getEmail() {
    return email;
  }
  public void setEmail(List<CreateEmailPayload> email) {
    this.email = email;
  }
  public List<CreateQueryPayload> getItemGrp() {
    return itemGrp;
  }
  public void setItemGrp(List<CreateQueryPayload> itemGrp) {
    this.itemGrp = itemGrp;
  }
  public String getCg_id() {
    return cg_id;
  }
  public void setCg_id(String cg_id) {
    this.cg_id = cg_id;
  }
}
