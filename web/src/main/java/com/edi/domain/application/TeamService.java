package com.edi.domain.application;

import com.edi.domain.application.commands.CreateTeamCommand;
import com.edi.domain.model.team.Team;
import com.edi.domain.model.user.UserId;

import java.util.List;

public interface TeamService {

  /**
   * Find the teams that created by a user
   *
   * @param userId the id of the user
   * @return a list of teams or an empty list if none found
   */
  List<Team> findTeamsByUserId(UserId userId);

  /**
   * Create a new team
   *
   * @param command the command instance
   * @return the newly created team
   */
  Team createTeam(CreateTeamCommand command);
}
