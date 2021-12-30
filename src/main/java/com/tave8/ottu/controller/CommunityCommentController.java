package com.tave8.ottu.controller;

import com.tave8.ottu.entity.Board;
import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.repository.BoardRepository;
import com.tave8.ottu.repository.CommentRepository;
import com.tave8.ottu.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/community/comment")
public class CommunityCommentController {

    @Autowired
    private CommentRepository commentRepository;
    private BoardRepository boardRepository;
    private BoardService boardService;

    // 댓글 작성
    @GetMapping("/{board_idx}")
    public ResponseEntity comment_write(@PathVariable("board_idx")Long board_idx, Comment commentForm){

        HashMap<String, Object> response = new HashMap<>();

        Comment comment = new Comment();

        comment.setComment_writer(comment.getComment_writer());
        if(commentForm.getComment() == null){
            response.put("success",false);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        else{
            Date now = new Date();
            comment.setComment_date(now);
            comment.setComment(commentForm.getComment());

            Board board = boardRepository.findById(board_idx).orElse(null);
            board.getComment().add(comment);

            commentRepository.saveAndFlush(comment);
            boardService.updateCommentList(board);

            response.put("success",true);
            return new ResponseEntity(HttpStatus.OK);
        }



    }
}
