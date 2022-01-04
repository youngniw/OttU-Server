package com.tave8.ottu.controller;
import com.tave8.ottu.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tave8.ottu.dto.CommentDTO;
import com.tave8.ottu.entity.Post;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/community")
public class CommunityCommentController {
    private final CommentService commentService;

    @Autowired
    public CommunityCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //댓글 작성
    @PostMapping("/comment/upload")
    private ResponseEntity writeComment(@RequestBody CommentDTO commentDTO){
        HashMap<String,Object> response = new HashMap<>();
        try{
            Comment comment = new Comment();
            // Post 등록
            Post post = new Post();
            post.setPostIdx(commentDTO.getPostIdx());
            comment.setPost(post);

            // 작성자 등록
            User user = new User();
            user.setUserIdx(commentDTO.getUserIdx());
            comment.setWriter(user);

            // 글 등록
            comment.setContent(commentDTO.getContent());

            commentService.save(comment);

            response.put("success",true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{ccid}")   //commentIdx
    public ResponseEntity deleteComment(@PathVariable("ccid") Long commentIdx){
        HashMap<String, Object> response = new HashMap<>();
        try {
            Comment comment = commentService.getCommentById(commentIdx);
            comment.setIsDeleted(true);
            if (commentService.saveCommentIsDeleted(comment)) {
                response.put("success", true);
                return new ResponseEntity(response, HttpStatus.OK);
            }
            else {
                response.put("success", false);
                return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
