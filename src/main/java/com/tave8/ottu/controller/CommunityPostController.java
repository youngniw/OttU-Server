package com.tave8.ottu.controller;

import com.tave8.ottu.entity.Board;
import com.tave8.ottu.repository.BoardRepository;
import com.tave8.ottu.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/community/post")
public class CommunityPostController {

    private BoardRepository boardRepository;
    private BoardService boardService;

    // 글 작성
    @GetMapping("/{writer}") // pathvariable로 작성자 가져오고, form에서 content가져오기.
    public ResponseEntity writeBoard(@PathVariable("writer")String writer, Board boardForm){

        HashMap<String, Object> response = new HashMap<>();

        //Board 객체 생성
        Board board = new Board();

        //현재 날짜 생성
        Date now = new Date();
        //BoardForm에서 가져온 내용이 비어있지 않을 경우.
        if(boardForm.getContent()!=null) {
            //board 객체에 작성자, content, 현재시간 넣어서 db에 저장
            boardService.write(board, writer, boardForm.getContent(), now);

            response.put("success",true);
            return new ResponseEntity(response, HttpStatus.OK);
        }

        else{
            // 내용이 비어있을 경우.
            new IllegalStateException("내용이 비어있습니다");
            response.put("success",false);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
