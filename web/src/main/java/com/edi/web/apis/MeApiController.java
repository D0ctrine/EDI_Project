package com.edi.web.apis;

import com.edi.domain.application.BoardService;
import com.edi.domain.application.TeamService;
import com.edi.domain.common.security.CurrentUser;
import com.edi.domain.model.board.Board;
import com.edi.domain.model.team.Team;
import com.edi.domain.model.user.SimpleUser;
import com.edi.web.results.ApiResult;
import com.edi.web.results.MyDataResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MeApiController {

  private TeamService teamService;
  private BoardService boardService;

  public MeApiController(TeamService teamService, BoardService boardService) {
    this.teamService = teamService;
    this.boardService = boardService;
  }

  @GetMapping("/api/me")
  public ResponseEntity<ApiResult> getMyData(@CurrentUser SimpleUser currentUser) {
    List<Team> teams = teamService.findTeamsByUserId(currentUser.getUserId());
    List<Board> boards = boardService.findBoardsByMembership(currentUser.getUserId());
    return MyDataResult.build(currentUser, teams, boards);
  }
}
