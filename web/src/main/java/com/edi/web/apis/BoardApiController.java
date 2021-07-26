package com.edi.web.apis;

import com.edi.domain.application.BoardService;
import com.edi.domain.common.security.CurrentUser;
import com.edi.domain.model.board.Board;
import com.edi.domain.model.user.SimpleUser;
import com.edi.web.payload.CreateBoardPayload;
import com.edi.web.results.ApiResult;
import com.edi.web.results.CreateBoardResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class BoardApiController {

  private BoardService boardService;

  public BoardApiController(BoardService boardService) {
    this.boardService = boardService;
  }

  @PostMapping("/api/boards")
  public ResponseEntity<ApiResult> createBoard(@RequestBody CreateBoardPayload payload,
                                               @CurrentUser SimpleUser currentUser) {
    Board board = boardService.createBoard(payload.toCommand(currentUser.getUserId()));
    return CreateBoardResult.build(board);
  }
}
