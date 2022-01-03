package com.tave8.ottu.controller;
import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tave8.ottu.dto.CommentDTO;
import com.tave8.ottu.entity.Post;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.repository.PostRepository;
import com.tave8.ottu.repository.UserRepository;
import com.tave8.ottu.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/community/comment")
public class CommunityCommentController {
    @Autowired
    private CommentService commentService;

    //댓글 작성
    @PostMapping("/upload")
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
            comment.setContent(commentDTO.getComment());

            // DB 등록
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
    @DeleteMapping("/{ccid}") // commentIdx
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

    // 댓글 목록
    @GetMapping("/{cpid}/list") // postIdx
    public ResponseEntity getCommentList(@PathVariable("cpid") Long postIdx){
        HashMap<String,Object> response = new HashMap<>();
        try {
            // isDeleted가 false인 comment찾아옴.
            List<Comment> commentList = commentService.getCommentList(postIdx);

            response.put("success",true);
            response.put("commentList",commentList);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
