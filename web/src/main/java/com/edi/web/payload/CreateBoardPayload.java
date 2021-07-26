package com.edi.web.payload;

import com.edi.domain.application.commands.CreateBoardCommand;
import com.edi.domain.model.team.TeamId;
import com.edi.domain.model.user.UserId;

public class CreateBoardPayload {

  private String name;
  private String description;
  private Long teamId;

  public CreateBoardCommand toCommand(UserId userId) {
    return new CreateBoardCommand(userId, name, description, new TeamId(teamId));
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

}
