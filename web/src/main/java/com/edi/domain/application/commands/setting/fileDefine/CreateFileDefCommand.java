package com.edi.domain.application.commands.setting.fileDefine;

import com.edi.domain.model.user.UserId;

public class CreateFileDefCommand {
  private UserId userId;
  private String cfgId;
  private String description;
  private String scheduleMonth;
  private String scheduleWeek;
  private String scheduleDay;
  private String scheduleHour;
  private String scheduleMin;
  private String fileExtractType;

public CreateFileDefCommand(UserId userId,String cfgId,String description,String scheduleMonth,String scheduleWeek,String scheduleDay,String scheduleHour,String scheduleMin,String fileExtractType){
  this.userId = userId;
  this.cfgId = cfgId;
  this.description = description;
  this.scheduleMonth = scheduleMonth;
  this.scheduleWeek = scheduleWeek;
  this.scheduleDay = scheduleDay;
  this.scheduleHour = scheduleHour;
  this.scheduleMin = scheduleMin;
  this.fileExtractType = fileExtractType;
}

public UserId getUserId() {
  return userId;
}

public String getCfgId() {
  return cfgId;
}

public String getDescription() {
  return description;
}

public String getScheduleMonth() {
  return scheduleMonth;
}

public String getScheduleWeek() {
  return scheduleWeek;
}

public String getScheduleDay() {
  return scheduleDay;
}

public String getScheduleHour() {
  return scheduleHour;
}

public String getScheduleMin() {
  return scheduleMin;
}

public String getFileExtractType() {
  return fileExtractType;
}

}
