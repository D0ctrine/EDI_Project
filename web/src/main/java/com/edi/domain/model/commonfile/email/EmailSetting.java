package com.edi.domain.model.commonfile.email;

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
@Table(name = "EDI_EMAIL_SETTING")
public class EmailSetting extends AbstractBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "EDI_EMAIL_SEQUENCE")
  @SequenceGenerator(name="EDI_EMAIL_SEQUENCE",sequenceName="edi_email_seq", allocationSize = 1)
  private Long id;

  @Column(name = "cfg_id")
  private String cfg_id;

  @Column(name = "host")
  private String host;

  @Column(name = "port")
  private String port;

  @Column(name = "access_id")
  private String access_id;

  @Column(name = "access_pwd")
  private String access_pwd;

  @Column(name = "create_user")
  private String create_user;

  @Column(name = "create_date")
  private String create_date;

  @Column(name = "update_user")
  private String update_user;

  @Column(name = "update_date")
  private String update_date;

  public static EmailSetting create(UserId userid,String cfg_id, String host, String port, String access_id, String access_pwd){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    EmailSetting eSetting = new EmailSetting();
    eSetting.setCreate_date(sqldate);
    eSetting.setCreate_user(userid.value().toString());
    eSetting.setCfg_id(cfg_id);
    eSetting.setHost(host);
    eSetting.setPort(port);
    eSetting.setAccess_id(access_id);
    eSetting.setAccess_pwd(access_pwd);

    return eSetting;
  }
  public static EmailSetting update(Long id,UserId userid,String cfg_id, String host, String port, String access_id, String access_pwd){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    EmailSetting eSetting = new EmailSetting();
    eSetting.setId(id);
    eSetting.setCfg_id(cfg_id);
    eSetting.setHost(host);
    eSetting.setPort(port);
    eSetting.setAccess_id(access_id);
    eSetting.setAccess_pwd(access_pwd);
    eSetting.setUpdate_date(sqldate);
    eSetting.setUpdate_user(userid.value().toString());

    return eSetting;
  }

  @Override
  public String toString() {
    return "EmailSetting [access_id=" + access_id + ", access_pwd=" + access_pwd + ", cfg_id=" + cfg_id
        + ", create_date=" + create_date + ", create_user=" + create_user + ", host=" + host + ", id=" + id + ", port="
        + port + ", update_date=" + update_date + ", update_user=" + update_user + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((access_id == null) ? 0 : access_id.hashCode());
    result = prime * result + ((access_pwd == null) ? 0 : access_pwd.hashCode());
    result = prime * result + ((cfg_id == null) ? 0 : cfg_id.hashCode());
    result = prime * result + ((create_date == null) ? 0 : create_date.hashCode());
    result = prime * result + ((create_user == null) ? 0 : create_user.hashCode());
    result = prime * result + ((host == null) ? 0 : host.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((port == null) ? 0 : port.hashCode());
    result = prime * result + ((update_date == null) ? 0 : update_date.hashCode());
    result = prime * result + ((update_user == null) ? 0 : update_user.hashCode());
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
    EmailSetting other = (EmailSetting) obj;
    if (access_id == null) {
      if (other.access_id != null)
        return false;
    } else if (!access_id.equals(other.access_id))
      return false;
    if (access_pwd == null) {
      if (other.access_pwd != null)
        return false;
    } else if (!access_pwd.equals(other.access_pwd))
      return false;
    if (cfg_id == null) {
      if (other.cfg_id != null)
        return false;
    } else if (!cfg_id.equals(other.cfg_id))
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
    if (host == null) {
      if (other.host != null)
        return false;
    } else if (!host.equals(other.host))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (port == null) {
      if (other.port != null)
        return false;
    } else if (!port.equals(other.port))
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

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getAccess_id() {
    return access_id;
  }

  public void setAccess_id(String access_id) {
    this.access_id = access_id;
  }

  public String getAccess_pwd() {
    return access_pwd;
  }

  public void setAccess_pwd(String access_pwd) {
    this.access_pwd = access_pwd;
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

}
