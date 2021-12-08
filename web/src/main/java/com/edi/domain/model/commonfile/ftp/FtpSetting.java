package com.edi.domain.model.commonfile.ftp;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.edi.domain.common.model.AbstractBaseEntity;
import com.edi.domain.model.user.UserId;

@Entity
@Table(name = "EDI_FTP_SETTING")
public class FtpSetting extends AbstractBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "EDI_FTP_SEQUENCE")
  @SequenceGenerator(name="EDI_FTP_SEQUENCE",sequenceName="edi_ftp_seq", allocationSize = 1)
  private Long id;

  @Column(name = "cfg_id")
  private String cfg_id;

  @Column(name = "ftp_addr")
  private String ftp_addr;

  @Column(name = "user_id")
  private String user_id;

  @Column(name = "pwd")
  private String pwd;

  @Column(name = "base_dir")
  private String base_dir;

  @Column(name = "type")
  private String type;

  @Column(name = "update_user")
  private String update_user;

  @Column(name = "update_date")
  private String update_date;

  @Column(name = "create_user")
  private String create_user;

  @Column(name = "create_date")
  private String create_date;

  public static FtpSetting create(UserId userid,String cfg_id, String ftp_addr, String user_id, String pwd, String base_dir, String type){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    FtpSetting ftpSetting = new FtpSetting();
    ftpSetting.setCreate_date(sqldate);
    ftpSetting.setCreate_user(userid.value().toString());
    ftpSetting.setCfg_id(cfg_id);
    ftpSetting.setFtp_addr(ftp_addr);
    ftpSetting.setUser_id(user_id);
    ftpSetting.setPwd(pwd);
    ftpSetting.setBase_dir(base_dir);
    ftpSetting.setType(type);
    return ftpSetting;
  }
  public static FtpSetting update(Long id,UserId userid,String cfg_id, String ftp_addr, String user_id, String pwd, String base_dir, String type){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    FtpSetting ftpSetting = new FtpSetting();
    ftpSetting.setId(id);
    ftpSetting.setCreate_user(userid.value().toString());
    ftpSetting.setCfg_id(cfg_id);
    ftpSetting.setFtp_addr(ftp_addr);
    ftpSetting.setUser_id(user_id);
    ftpSetting.setPwd(pwd);
    ftpSetting.setBase_dir(base_dir);
    ftpSetting.setType(type);
    ftpSetting.setUpdate_date(sqldate);
    ftpSetting.setUpdate_user(userid.value().toString());
    return ftpSetting;
  }

  @Override
  public String toString() {
    return "FtpSetting [base_dir=" + base_dir + ", create_date=" + create_date + ", create_user=" + create_user
        + ", cfg_id=" + cfg_id + ", ftp_addr=" + ftp_addr + ", id=" + id + ", pwd=" + pwd + ", type=" + type
        + ", update_date=" + update_date + ", update_user=" + update_user + ", user_id=" + user_id + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((base_dir == null) ? 0 : base_dir.hashCode());
    result = prime * result + ((create_date == null) ? 0 : create_date.hashCode());
    result = prime * result + ((create_user == null) ? 0 : create_user.hashCode());
    result = prime * result + ((cfg_id == null) ? 0 : cfg_id.hashCode());
    result = prime * result + ((ftp_addr == null) ? 0 : ftp_addr.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((pwd == null) ? 0 : pwd.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((update_date == null) ? 0 : update_date.hashCode());
    result = prime * result + ((update_user == null) ? 0 : update_user.hashCode());
    result = prime * result + ((user_id == null) ? 0 : user_id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FtpSetting other = (FtpSetting) obj;
    if (base_dir == null) {
      if (other.base_dir != null)
        return false;
    } else if (!base_dir.equals(other.base_dir))
      return false;
    if (create_date == null) {
      if (other.create_date != null)
        return false;
    } else if (!create_date.equals(other.create_date))
      return false;
    if (create_user == null) {
      if (other.create_user != null)
        return false;
    } else if (!create_user.equals(other.create_user))
      return false;
    if (cfg_id == null) {
      if (other.cfg_id != null)
        return false;
    } else if (!cfg_id.equals(other.cfg_id))
      return false;
    if (ftp_addr == null) {
      if (other.ftp_addr != null)
        return false;
    } else if (!ftp_addr.equals(other.ftp_addr))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (pwd == null) {
      if (other.pwd != null)
        return false;
    } else if (!pwd.equals(other.pwd))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (update_date == null) {
      if (other.update_date != null)
        return false;
    } else if (!update_date.equals(other.update_date))
      return false;
    if (update_user == null) {
      if (other.update_user != null)
        return false;
    } else if (!update_user.equals(other.update_user))
      return false;
    if (user_id == null) {
      if (other.user_id != null)
        return false;
    } else if (!user_id.equals(other.user_id))
      return false;
    return true;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCfg_id() {
    return cfg_id;
  }

  public void setCfg_id(String cfg_id) {
    this.cfg_id = cfg_id;
  }

  public String getFtp_addr() {
    return ftp_addr;
  }

  public void setFtp_addr(String ftp_addr) {
    this.ftp_addr = ftp_addr;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getPwd() {
    return pwd;
  }

  public void setPwd(String pwd) {
    this.pwd = pwd;
  }

  public String getBase_dir() {
    return base_dir;
  }

  public void setBase_dir(String base_dir) {
    this.base_dir = base_dir;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUpdate_user() {
    return update_user;
  }

  public void setUpdate_user(String update_user) {
    this.update_user = update_user;
  }

  public String getUpdate_date() {
    return update_date;
  }

  public void setUpdate_date(String update_date) {
    this.update_date = update_date;
  }

  public String getCreate_user() {
    return create_user;
  }

  public void setCreate_user(String create_user) {
    this.create_user = create_user;
  }

  public String getCreate_date() {
    return create_date;
  }

  public void setCreate_date(String create_date) {
    this.create_date = create_date;
  }

}
