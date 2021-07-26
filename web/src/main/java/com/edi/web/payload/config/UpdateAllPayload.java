package com.edi.web.payload.config;

import java.util.List;

public class UpdateAllPayload {
  private List<UpdateEnvPayload> updtEnv;
  private List<UpdateFtpPayload> updtFtp;
  private List<UpdateEmailPayload> updtEmail;
  private List<UpdateQueryPayload> updtItemGrp;
  private String cg_id;

  public String getCg_id() {
    return cg_id;
  }
  public void setCg_id(String cg_id) {
    this.cg_id = cg_id;
  }

  public List<UpdateEnvPayload> getUpdtEnv() {
    return updtEnv;
  }

  public void setUpdtEnv(List<UpdateEnvPayload> updtEnv) {
    this.updtEnv = updtEnv;
  }

  public List<UpdateFtpPayload> getUpdtFtp() {
    return updtFtp;
  }

  public void setUpdtFtp(List<UpdateFtpPayload> updtFtp) {
    this.updtFtp = updtFtp;
  }

  public List<UpdateEmailPayload> getUpdtEmail() {
    return updtEmail;
  }

  public void setUpdtEmail(List<UpdateEmailPayload> updtEmail) {
    this.updtEmail = updtEmail;
  }

  public List<UpdateQueryPayload> getUpdtItemGrp() {
    return updtItemGrp;
  }

  public void setUpdtItemGrp(List<UpdateQueryPayload> updtItemGrp) {
    this.updtItemGrp = updtItemGrp;
  }

}
