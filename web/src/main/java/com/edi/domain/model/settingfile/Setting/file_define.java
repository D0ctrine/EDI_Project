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
@Table(name = "EDI_FILE_DEFINE")
public class file_define extends AbstractBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EDI_FDEF_SEQUENCE")
  @SequenceGenerator(name = "EDI_FDEF_SEQUENCE", sequenceName = "edi_fdef_seq", allocationSize = 1)
  private Long id;

  @Column(name = "cfg_id")
  private String cfg_id;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "file_desc")
  private String file_desc;

  @Column(name = "file_save_folder")
  private String fileSaveFolder;

  @Column(name = "file_charset")
  private String fileCharset;

  @Column(name = "file_type")
  private String fileType;

  @Column(name = "data_type")
  private String dataType;

  @Column(name = "ftp_env_id")
  private String ftpEnvId;

  @Column(name = "send_flag")
  private String send_flag;

  @Column(name = "nodatasend")
  private String noDataSend;

  @Column(name = "schedule_min")
  private String scheduleMin;

  @Column(name = "schedule_hour")
  private String scheduleHour;

  @Column(name = "schedule_day")
  private String scheduleDay;

  @Column(name = "schedule_week")
  private String scheduleWeek;

  @Column(name = "schedule_month")
  private String scheduleMonth;

  @Column(name = "cm_f01")
  private String cm_f01;

  @Column(name = "cm_f02")
  private String cm_f02;

  @Column(name = "create_user")
  private String create_user;

  @Column(name = "create_date")
  private String create_date;

  @Column(name = "update_user")
  private String update_user;

  @Column(name = "update_date")
  private String update_date;

  @Column(name = "delete_user")
  private String delete_user;

  @Column(name = "delete_date")
  private String delete_date;

  public static file_define create(UserId userid, String cfg_id, String fileName, String file_desc, String fileSaveFolder, String fileCharset, String dataType
                                  , String fileType, String ftpEnvId,String scheduleMin, String scheduleHour, String scheduleDay
                                  , String scheduleWeek, String scheduleMonth,String noDataSend,String send_flag) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    file_define fDefine = new file_define();
    fDefine.setCfg_id(cfg_id);
    fDefine.setFileName(fileName);
    fDefine.setFile_desc(file_desc);
    fDefine.setFileSaveFolder(fileSaveFolder);
    fDefine.setFileCharset(fileCharset);
    fDefine.setDataType(dataType);
    fDefine.setFileType(fileType);
    fDefine.setFtpEnvId(ftpEnvId);
    fDefine.setScheduleMin(scheduleMin);
    fDefine.setScheduleHour(scheduleHour);
    fDefine.setScheduleDay(scheduleDay);
    fDefine.setScheduleWeek(scheduleWeek);
    fDefine.setScheduleMonth(scheduleMonth);
    fDefine.setCreate_date(sqldate);
    fDefine.setCreate_user(userid.value().toString());
    fDefine.setNoDataSend(noDataSend);
    fDefine.setSend_flag(send_flag);
    return fDefine;
  }

  public static file_define update(Long id, UserId userid, String cfg_id, String fileName, String file_desc, String fileSaveFolder, String fileCharset, String dataType
                                  , String fileType, String ftpEnvId,String scheduleMin, String scheduleHour, String scheduleDay
                                  , String scheduleWeek, String scheduleMonth,String noDataSend) {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String sqldate = formatter.format(date);

    file_define fDefine = new file_define();
    fDefine.setId(id);
    fDefine.setCfg_id(cfg_id);
    fDefine.setFileName(fileName);
    fDefine.setFile_desc(file_desc);
    fDefine.setFileSaveFolder(fileSaveFolder);
    fDefine.setFileCharset(fileCharset);
    fDefine.setDataType(dataType);
    fDefine.setFileType(fileType);
    fDefine.setFtpEnvId(ftpEnvId);
    fDefine.setScheduleMin(scheduleMin);
    fDefine.setScheduleHour(scheduleHour);
    fDefine.setScheduleDay(scheduleDay);
    fDefine.setScheduleWeek(scheduleWeek);
    fDefine.setScheduleMonth(scheduleMonth);
    fDefine.setUpdate_date(sqldate);
    fDefine.setUpdate_user(userid.value().toString());
    fDefine.setNoDataSend(noDataSend);

    return fDefine;
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

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFile_desc() {
    return file_desc;
  }

  public void setFile_desc(String file_desc) {
    this.file_desc = file_desc;
  }

  public String getFileSaveFolder() {
    return fileSaveFolder;
  }

  public void setFileSaveFolder(String fileSaveFolder) {
    this.fileSaveFolder = fileSaveFolder;
  }

  public String getFileCharset() {
    return fileCharset;
  }

  public void setFileCharset(String fileCharset) {
    this.fileCharset = fileCharset;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getFtpEnvId() {
    return ftpEnvId;
  }

  public void setFtpEnvId(String ftpEnvId) {
    this.ftpEnvId = ftpEnvId;
  }

  public String getSend_flag() {
    return send_flag;
  }

  public void setSend_flag(String send_flag) {
    this.send_flag = send_flag;
  }

  public String getNoDataSend() {
    return noDataSend;
  }

  public void setNoDataSend(String noDataSend) {
    this.noDataSend = noDataSend;
  }

  public String getScheduleMin() {
    return scheduleMin;
  }

  public void setScheduleMin(String scheduleMin) {
    this.scheduleMin = scheduleMin;
  }

  public String getScheduleHour() {
    return scheduleHour;
  }

  public void setScheduleHour(String scheduleHour) {
    this.scheduleHour = scheduleHour;
  }

  public String getScheduleDay() {
    return scheduleDay;
  }

  public void setScheduleDay(String scheduleDay) {
    this.scheduleDay = scheduleDay;
  }

  public String getScheduleWeek() {
    return scheduleWeek;
  }

  public void setScheduleWeek(String scheduleWeek) {
    this.scheduleWeek = scheduleWeek;
  }

  public String getScheduleMonth() {
    return scheduleMonth;
  }

  public void setScheduleMonth(String scheduleMonth) {
    this.scheduleMonth = scheduleMonth;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cfg_id == null) ? 0 : cfg_id.hashCode());
    result = prime * result + ((cm_f01 == null) ? 0 : cm_f01.hashCode());
    result = prime * result + ((cm_f02 == null) ? 0 : cm_f02.hashCode());
    result = prime * result + ((create_date == null) ? 0 : create_date.hashCode());
    result = prime * result + ((create_user == null) ? 0 : create_user.hashCode());
    result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
    result = prime * result + ((delete_date == null) ? 0 : delete_date.hashCode());
    result = prime * result + ((delete_user == null) ? 0 : delete_user.hashCode());
    result = prime * result + ((fileCharset == null) ? 0 : fileCharset.hashCode());
    result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
    result = prime * result + ((fileSaveFolder == null) ? 0 : fileSaveFolder.hashCode());
    result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
    result = prime * result + ((file_desc == null) ? 0 : file_desc.hashCode());
    result = prime * result + ((ftpEnvId == null) ? 0 : ftpEnvId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((noDataSend == null) ? 0 : noDataSend.hashCode());
    result = prime * result + ((scheduleDay == null) ? 0 : scheduleDay.hashCode());
    result = prime * result + ((scheduleHour == null) ? 0 : scheduleHour.hashCode());
    result = prime * result + ((scheduleMin == null) ? 0 : scheduleMin.hashCode());
    result = prime * result + ((scheduleMonth == null) ? 0 : scheduleMonth.hashCode());
    result = prime * result + ((scheduleWeek == null) ? 0 : scheduleWeek.hashCode());
    result = prime * result + ((send_flag == null) ? 0 : send_flag.hashCode());
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
    if (dataType == null) {
      if (other.dataType != null)
        return false;
    } else if (!dataType.equals(other.dataType))
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
    if (fileCharset == null) {
      if (other.fileCharset != null)
        return false;
    } else if (!fileCharset.equals(other.fileCharset))
      return false;
    if (fileName == null) {
      if (other.fileName != null)
        return false;
    } else if (!fileName.equals(other.fileName))
      return false;
    if (fileSaveFolder == null) {
      if (other.fileSaveFolder != null)
        return false;
    } else if (!fileSaveFolder.equals(other.fileSaveFolder))
      return false;
    if (fileType == null) {
      if (other.fileType != null)
        return false;
    } else if (!fileType.equals(other.fileType))
      return false;
    if (file_desc == null) {
      if (other.file_desc != null)
        return false;
    } else if (!file_desc.equals(other.file_desc))
      return false;
    if (ftpEnvId == null) {
      if (other.ftpEnvId != null)
        return false;
    } else if (!ftpEnvId.equals(other.ftpEnvId))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (noDataSend == null) {
      if (other.noDataSend != null)
        return false;
    } else if (!noDataSend.equals(other.noDataSend))
      return false;
    if (scheduleDay == null) {
      if (other.scheduleDay != null)
        return false;
    } else if (!scheduleDay.equals(other.scheduleDay))
      return false;
    if (scheduleHour == null) {
      if (other.scheduleHour != null)
        return false;
    } else if (!scheduleHour.equals(other.scheduleHour))
      return false;
    if (scheduleMin == null) {
      if (other.scheduleMin != null)
        return false;
    } else if (!scheduleMin.equals(other.scheduleMin))
      return false;
    if (scheduleMonth == null) {
      if (other.scheduleMonth != null)
        return false;
    } else if (!scheduleMonth.equals(other.scheduleMonth))
      return false;
    if (scheduleWeek == null) {
      if (other.scheduleWeek != null)
        return false;
    } else if (!scheduleWeek.equals(other.scheduleWeek))
      return false;
    if (send_flag == null) {
      if (other.send_flag != null)
        return false;
    } else if (!send_flag.equals(other.send_flag))
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

  @Override
  public String toString() {
    return "file_define [cfg_id=" + cfg_id + ", cm_f01=" + cm_f01 + ", cm_f02=" + cm_f02 + ", create_date="
        + create_date + ", create_user=" + create_user + ", dataType=" + dataType + ", delete_date=" + delete_date
        + ", delete_user=" + delete_user + ", fileCharset=" + fileCharset + ", fileName=" + fileName
        + ", fileSaveFolder=" + fileSaveFolder + ", fileType=" + fileType + ", file_desc=" + file_desc + ", ftpEnvId="
        + ftpEnvId + ", id=" + id + ", noDataSend=" + noDataSend + ", scheduleDay=" + scheduleDay + ", scheduleHour="
        + scheduleHour + ", scheduleMin=" + scheduleMin + ", scheduleMonth=" + scheduleMonth + ", scheduleWeek="
        + scheduleWeek + ", send_flag=" + send_flag + ", update_date=" + update_date + ", update_user=" + update_user
        + "]";
  }

}
