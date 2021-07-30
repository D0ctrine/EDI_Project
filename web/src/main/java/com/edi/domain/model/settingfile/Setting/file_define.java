package com.edi.domain.model.settingfile.Setting;

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
@Table(name = "file_define")
public class file_define extends AbstractBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDI_FDEF_SEQUENCE")
  @SequenceGenerator(name = "EDI_FDEF_SEQUENCE", sequenceName = "edi_fdef_seq", allocationSize = 1)
  private Long id;

  @Column(name = "cfg_id")
  private String cfg_id;

  @Column(name = "file_desc")
  private String file_desc;

  @Column(name = "cron_data")
  private String cron_data;

  @Column(name = "extract_type")
  private String extract_type;

  @Column(name = "cm_f01")
  private String cm_f01;

  @Column(name = "cm_f02")
  private String cm_f02;

  @Column(name = "update_user")
  private String update_user;

  @Column(name = "update_date")
  private String update_date;

  @Column(name = "delete_user")
  private String delete_user;

  @Column(name = "delete_date")
  private String delete_date;

  @Column(name = "create_user")
  private String create_user;

  @Column(name = "create_date")
  private String create_date;

  public static file_define create(UserId userid, String cfg_id, String file_desc, String cron_data,
      String extract_type) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    file_define fDefine = new file_define();
    fDefine.setCfg_id(cfg_id);
    fDefine.setUpdate_date(sqldate);
    fDefine.setUpdate_user(userid.value().toString());
    fDefine.setFile_desc(file_desc);
    fDefine.setCron_data(cron_data);
    fDefine.setExtract_type(extract_type);

    return fDefine;
  }

  public static file_define update(Long id, UserId userid, String cfg_id, String file_desc, String cron_data,
      String extract_type) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    file_define fDefine = new file_define();
    fDefine.setId(id);
    fDefine.setCfg_id(cfg_id);
    fDefine.setUpdate_date(sqldate);
    fDefine.setUpdate_user(userid.value().toString());
    fDefine.setFile_desc(file_desc);
    fDefine.setCron_data(cron_data);
    fDefine.setExtract_type(extract_type);

    return fDefine;
  }

  @Override
  public String toString() {
    return "file_define [cm_f01=" + cm_f01 + ", cm_f02=" + cm_f02 + ", create_date=" + create_date + ", create_user="
        + create_user + ", extract_type=" + extract_type + ", file_desc=" + file_desc + ", id=" + id + ", cron_data="
        + cron_data + ", cfg_id=" + cfg_id + ", update_date=" + update_date + ", update_user=" + update_user + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cfg_id == null) ? 0 : cfg_id.hashCode());
    result = prime * result + ((cm_f01 == null) ? 0 : cm_f01.hashCode());
    result = prime * result + ((cm_f02 == null) ? 0 : cm_f02.hashCode());
    result = prime * result + ((create_date == null) ? 0 : create_date.hashCode());
    result = prime * result + ((create_user == null) ? 0 : create_user.hashCode());
    result = prime * result + ((cron_data == null) ? 0 : cron_data.hashCode());
    result = prime * result + ((delete_date == null) ? 0 : delete_date.hashCode());
    result = prime * result + ((delete_user == null) ? 0 : delete_user.hashCode());
    result = prime * result + ((extract_type == null) ? 0 : extract_type.hashCode());
    result = prime * result + ((file_desc == null) ? 0 : file_desc.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    file_define other = (file_define) obj;
    if (cfg_id == null) {
      if (other.cfg_id != null)
        return false;
    } else if (!cfg_id.equals(other.cfg_id))
      return false;
    if (cm_f01 == null) {
      if (other.cm_f01 != null)
        return false;
    } else if (!cm_f01.equals(other.cm_f01))
      return false;
    if (cm_f02 == null) {
      if (other.cm_f02 != null)
        return false;
    } else if (!cm_f02.equals(other.cm_f02))
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
    if (cron_data == null) {
      if (other.cron_data != null)
        return false;
    } else if (!cron_data.equals(other.cron_data))
      return false;
    if (delete_date == null) {
      if (other.delete_date != null)
        return false;
    } else if (!delete_date.equals(other.delete_date))
      return false;
    if (delete_user == null) {
      if (other.delete_user != null)
        return false;
    } else if (!delete_user.equals(other.delete_user))
      return false;
    if (extract_type == null) {
      if (other.extract_type != null)
        return false;
    } else if (!extract_type.equals(other.extract_type))
      return false;
    if (file_desc == null) {
      if (other.file_desc != null)
        return false;
    } else if (!file_desc.equals(other.file_desc))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
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

  public String getFile_desc() {
    return file_desc;
  }

  public void setFile_desc(String file_desc) {
    this.file_desc = file_desc;
  }

  public String getCron_data() {
    return cron_data;
  }

  public void setCron_data(String cron_data) {
    this.cron_data = cron_data;
  }

  public String getExtract_type() {
    return extract_type;
  }

  public void setExtract_type(String extract_type) {
    this.extract_type = extract_type;
  }

  public String getCm_f01() {
    return cm_f01;
  }

  public void setCm_f01(String cm_f01) {
    this.cm_f01 = cm_f01;
  }

  public String getCm_f02() {
    return cm_f02;
  }

  public void setCm_f02(String cm_f02) {
    this.cm_f02 = cm_f02;
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

  public String getDelete_user() {
    return delete_user;
  }

  public void setDelete_user(String delete_user) {
    this.delete_user = delete_user;
  }

  public String getDelete_date() {
    return delete_date;
  }

  public void setDelete_date(String delete_date) {
    this.delete_date = delete_date;
  }

}
