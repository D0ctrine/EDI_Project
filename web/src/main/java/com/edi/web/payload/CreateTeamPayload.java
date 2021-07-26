package com.edi.web.payload;

import com.edi.domain.application.commands.CreateTeamCommand;
import com.edi.domain.model.user.UserId;

public class CreateTeamPayload {

  private String name;

  public CreateTeamCommand toCommand(UserId userId) {
    return new CreateTeamCommand(userId, name);
  }

  public void setName(String name) {
    this.name = name;
  }
}
