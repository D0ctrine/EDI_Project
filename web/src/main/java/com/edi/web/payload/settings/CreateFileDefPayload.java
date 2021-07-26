package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.fileDefine.CreateFileDefCommand;
import com.edi.domain.model.user.UserId;

public class CreateFileDefPayload {
  private String cfgId;
  private String fileDesc;
  private String scheduleMonth;
  private String scheduleWeek;
  private String scheduleDay;
  private String scheduleHour;
  private String scheduleMin;
  private String extractType;

  public CreateFileDefCommand toCommand(UserId userId) {
    return new CreateFileDefCommand(userId, cfgId, fileDesc, scheduleMonth, scheduleWeek, scheduleDay, scheduleHour, scheduleMin, extractType);
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

  public void setFileDesc(String fileDesc) {
    this.fileDesc = fileDesc;
  }

  public void setScheduleMonth(String scheduleMonth) {
    this.scheduleMonth = scheduleMonth;
  }

  public void setScheduleWeek(String scheduleWeek) {
    this.scheduleWeek = scheduleWeek;
  }

  public void setScheduleDay(String scheduleDay) {
    this.scheduleDay = scheduleDay;
  }

  public void setScheduleHour(String scheduleHour) {
    this.scheduleHour = scheduleHour;
  }

  public void setScheduleMin(String scheduleMin) {
    this.scheduleMin = scheduleMin;
  }

  public void setExtractType(String extractType) {
    this.extractType = extractType;
  }

}
