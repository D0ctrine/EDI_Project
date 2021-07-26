package com.edi.web.apis;

import com.edi.domain.application.TeamService;
import com.edi.domain.common.security.CurrentUser;
import com.edi.domain.model.team.Team;
import com.edi.domain.model.user.SimpleUser;
import com.edi.web.payload.CreateTeamPayload;
import com.edi.web.results.ApiResult;
import com.edi.web.results.CreateTeamResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TeamApiController {

  private TeamService teamService;

  public TeamApiController(TeamService teamService) {
    this.teamService = teamService;
  }

  @PostMapping("/api/teams")
  public ResponseEntity<ApiResult> createTeam(@RequestBody CreateTeamPayload payload,
                                              @CurrentUser SimpleUser currentUser) {
    Team team = teamService.createTeam(payload.toCommand(currentUser.getUserId()));
    return CreateTeamResult.build(team);
  }
}
