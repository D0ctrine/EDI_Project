package com.edi.domain.model.commonfile.environment;

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
@Table(name = "EDI_ENV_SETTING")
public class EnvSetting extends AbstractBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "EDI_ENV_SEQUENCE")
  @SequenceGenerator(name="EDI_ENV_SEQUENCE",sequenceName="edi_env_seq", allocationSize = 1)
  private Long id;

  @Column(name = "cfg_id")
  private String cfg_id;

  @Column(name = "item")
  private String item;

  @Column(name = "set_type")
  private String set_type;

  @Column(name = "value")
  private String value;

  @Column(name = "note")
  private String note;

  @Column(name = "create_user")
  private String create_user;

  @Column(name = "create_date")
  private String create_date;

  @Column(name = "update_user")
  private String update_user;

  @Column(name = "update_date")
  private String update_date;

  public static EnvSetting create(UserId userid,String cfg_id, String item, String value, String note, String set_type){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    EnvSetting eSetting = new EnvSetting();
    eSetting.setCreate_date(sqldate);
    eSetting.setCreate_user(userid.value().toString());
    eSetting.setCfg_id(cfg_id);
    eSetting.setItem(item);
    eSetting.setValue(value);
    eSetting.setNote(note);
    eSetting.setSet_type(set_type);

    return eSetting;
  }

  public static EnvSetting update(Long id,UserId userid,String cfg_id, String item, String value, String note, String set_type){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    EnvSetting eSetting = new EnvSetting();
    eSetting.setId(id);
    eSetting.setCreate_user(userid.value().toString());
    eSetting.setCfg_id(cfg_id);
    eSetting.setItem(item);
    eSetting.setValue(value);
    eSetting.setNote(note);
    eSetting.setUpdate_date(sqldate);
    eSetting.setUpdate_user(userid.value().toString());
    eSetting.setSet_type(set_type);
    return eSetting;
  }

  @Override
  public String toString() {
    return "EnvSetting [cfg_id=" + cfg_id + ", create_date=" + create_date + ", create_user=" + create_user + ", id="
        + id + ", item=" + item + ", note=" + note + ", update_date=" + update_date + ", update_user=" + update_user
        + ", value=" + value + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cfg_id == null) ? 0 : cfg_id.hashCode());
    result = prime * result + ((create_date == null) ? 0 : create_date.hashCode());
    result = prime * result + ((create_user == null) ? 0 : create_user.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((item == null) ? 0 : item.hashCode());
    result = prime * result + ((note == null) ? 0 : note.hashCode());
    result = prime * result + ((set_type == null) ? 0 : set_type.hashCode());
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
    EnvSetting other = (EnvSetting) obj;
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
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (item == null) {
      if (other.item != null)
        return false;
    } else if (!item.equals(other.item))
      return false;
    if (note == null) {
      if (other.note != null)
        return false;
    } else if (!note.equals(other.note))
      return false;
    if (set_type == null) {
      if (other.set_type != null)
        return false;
    } else if (!set_type.equals(other.set_type))
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

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public String getSet_type() {
    return set_type;
  }

  public void setSet_type(String set_type) {
    this.set_type = set_type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
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
