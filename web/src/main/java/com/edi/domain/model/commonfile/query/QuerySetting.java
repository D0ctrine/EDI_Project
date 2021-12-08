package com.edi.domain.model.commonfile.query;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.edi.domain.common.model.AbstractBaseEntity;
import com.edi.domain.model.user.UserId;

@Entity
@Table(name = "EDI_QUERY_SETTING")
public class QuerySetting extends AbstractBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "EDI_QUERY_SEQUENCE")
  @SequenceGenerator(name="EDI_QUERY_SEQUENCE",sequenceName="edi_query_seq", allocationSize = 1)
  private Long id;

  @Column(name = "setting_id")
  private String setting_id;

  @Column(name = "key")
  private String key;

// value: value값 그 자체, smallQ: 추가적으로 필요한 데이터를 위해 사용하는 쿼리, bigQ: 파일 핵심데이터, function: 약속된 함수
  @Column(name = "type")
  private String type;

  @Column(name = "query")
  @Lob
  private String query;

  @Column(name = "create_user")
  private String create_user;

  @Column(name = "create_date")
  private String create_date;

  @Column(name = "dbtype")
  private String dbType;

  public static QuerySetting create(UserId userid,String cfg_id, String key, String type, String query, String dbType){
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    QuerySetting querySetting = new QuerySetting();
    querySetting.setCreate_date(sqldate);
    querySetting.setCreate_user(userid.value().toString());
    querySetting.setSetting_id(cfg_id);
    querySetting.setKey(key);
    querySetting.setType(type);
    querySetting.setQuery(query);
    querySetting.setDbtype(dbType);

    return querySetting;
  }
  public static QuerySetting update(Long id,UserId userid,String cfg_id, String key, String type, String query, String db_type){
    QuerySetting querySetting = new QuerySetting();
    querySetting.setId(id);
    querySetting.setCreate_user(userid.value().toString());
    querySetting.setSetting_id(cfg_id);
    querySetting.setKey(key);
    querySetting.setType(type);
    querySetting.setQuery(query);
    querySetting.setDbtype(db_type);

    return querySetting;
  }

  @Override
  public String toString() {
    return "QuerySetting [create_date=" + create_date + ", create_user=" + create_user + ", dbtype=" + dbType
        + ", id=" + id + ", key=" + key + ", query=" + query + ", setting_id=" + setting_id + ", type=" + type + "]";
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((create_date == null) ? 0 : create_date.hashCode());
    result = prime * result + ((create_user == null) ? 0 : create_user.hashCode());
    result = prime * result + ((dbType == null) ? 0 : dbType.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((query == null) ? 0 : query.hashCode());
    result = prime * result + ((setting_id == null) ? 0 : setting_id.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    QuerySetting other = (QuerySetting) obj;
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
    if (dbType == null) {
      if (other.dbType != null)
        return false;
    } else if (!dbType.equals(other.dbType))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    if (query == null) {
      if (other.query != null)
        return false;
    } else if (!query.equals(other.query))
      return false;
    if (setting_id == null) {
      if (other.setting_id != null)
        return false;
    } else if (!setting_id.equals(other.setting_id))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSetting_id() {
    return setting_id;
  }

  public void setSetting_id(String setting_id) {
    this.setting_id = setting_id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
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
  public String getDbtype() {
    return dbType;
  }
  public void setDbtype(String db_type) {
    this.dbType = db_type;
  }

}
