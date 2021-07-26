package com.edi.web.payload.settings;

import com.edi.domain.application.commands.setting.fileDefine.UpdateFileDefCommand;
import com.edi.domain.model.user.UserId;

public class UpdateFileDefPayload {
  private Long id;
  private String cfgId;
  private String fileDesc;
  private String scheduleMonth;
  private String scheduleWeek;
  private String scheduleDay;
  private String scheduleHour;
  private String scheduleMin;
  private String extractType;

  public UpdateFileDefCommand toCommand(UserId userId){
    return new UpdateFileDefCommand(id, userId, cfgId, fileDesc, scheduleMonth, scheduleWeek, scheduleDay, scheduleHour, scheduleMin, extractType);
  }

  public Long getId() {
    return id;
  }

  public String getCfgId() {
    return cfgId;
  }

  public String getFileDesc() {
    return fileDesc;
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

  public String getExtractType() {
    return extractType;
  }

  public void setCfgId(String cfgId) {
    this.cfgId = cfgId;
  }

}
