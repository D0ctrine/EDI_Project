package com.edi.domain.model.settingfile.headntail;

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
@Table(name = "EDI_FILE_HEADNTAIL")
public class headntail extends AbstractBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDI_HNT_SEQUENCE")
  @SequenceGenerator(name = "EDI_HNT_SEQUENCE", sequenceName = "edi_hnt_seq", allocationSize = 1)
  private Long id;

  @Column(name = "cfg_id")
  private String cfg_id;

  // type : h_text n t_text or h_sql n t_sql
  @Column(name = "data_type")
  private String data_type;

  @Column(name = "note")
  private String note;

  @Column(name = "value")
  private String value;

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

  public static headntail create(UserId userid, String cfg_id, String data_type, String note, String value) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    headntail hnt = new headntail();
    hnt.setCfg_id(cfg_id);
    hnt.setCreate_date(sqldate);
    hnt.setCreate_user(userid.value().toString());
    hnt.setData_type(data_type);
    hnt.setNote(note);
    hnt.setValue(value);

    return hnt;
  }

  public static headntail update(Long id, UserId userid, String cfg_id, String data_type, String note, String value) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    headntail hnt = new headntail();
    hnt.setId(id);
    hnt.setCfg_id(cfg_id);
    hnt.setCreate_date(sqldate);
    hnt.setCreate_user(userid.value().toString());
    hnt.setData_type(data_type);
    hnt.setNote(note);
    hnt.setValue(value);

    return hnt;
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
    result = prime * result + ((data_type == null) ? 0 : data_type.hashCode());
    result = prime * result + ((delete_date == null) ? 0 : delete_date.hashCode());
    result = prime * result + ((delete_user == null) ? 0 : delete_user.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((note == null) ? 0 : note.hashCode());
    result = prime * result + ((update_date == null) ? 0 : update_date.hashCode());
    result = prime * result + ((update_user == null) ? 0 : update_user.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    headntail other = (headntail) obj;
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
    if (data_type == null) {
      if (other.data_type != null)
        return false;
    } else if (!data_type.equals(other.data_type))
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
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (note == null) {
      if (other.note != null)
        return false;
    } else if (!note.equals(other.note))
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
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "headntail [cfg_id=" + cfg_id + ", cm_f01=" + cm_f01 + ", cm_f02=" + cm_f02 + ", create_date=" + create_date
        + ", create_user=" + create_user + ", data_type=" + data_type + ", delete_date=" + delete_date
        + ", delete_user=" + delete_user + ", id=" + id + ", note=" + note + ", update_date=" + update_date
        + ", update_user=" + update_user + ", value=" + value + "]";
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

  public String getData_type() {
    return data_type;
  }

  public void setData_type(String data_type) {
    this.data_type = data_type;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
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
