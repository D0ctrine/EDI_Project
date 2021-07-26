package com.edi.domain.application.impl;

import com.edi.domain.application.TeamService;
import com.edi.domain.application.commands.CreateTeamCommand;
import com.edi.domain.common.event.DomainEventPublisher;
import com.edi.domain.model.team.Team;
import com.edi.domain.model.team.TeamRepository;
import com.edi.domain.model.team.events.TeamCreatedEvent;
import com.edi.domain.model.user.UserId;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

  private TeamRepository teamRepository;
  private DomainEventPublisher domainEventPublisher;

  public TeamServiceImpl(TeamRepository teamRepository, DomainEventPublisher domainEventPublisher) {
    this.teamRepository = teamRepository;
    this.domainEventPublisher = domainEventPublisher;
  }

  @Override
  public List<Team> findTeamsByUserId(UserId userId) {
    return teamRepository.findTeamsByUserId(userId);
  }

  @Override
  public Team createTeam(CreateTeamCommand command) {
    Team team = Team.create(command.getName(), command.getUserId());
    teamRepository.save(team);
    domainEventPublisher.publish(new TeamCreatedEvent(this, team));
    return team;
  }
}
