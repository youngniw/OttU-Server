package com.tave8.ottu.controller;
import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.repository.CommentRepository;
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

    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private PostRepository postRepository;
    private CommentService commentService;

    // 댓글 작성
    @PostMapping("/write")
    private ResponseEntity writeComment(@RequestBody CommentDTO commentDTO){

        HashMap<String,Object> response = new HashMap<>();

        try{
            Comment comment = new Comment();
            // 작성자 등록
            comment.setWriter(userRepository.findUserByUserIdx(commentDTO.getUserIdx()).orElse(null));
            // 글 등록
            if(commentDTO.getComment()==null){
                new IllegalStateException("댓글 내용이 없습니다.");
                response.put("success",false);
                return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
            }
            comment.setContent(commentDTO.getComment());
            // user 등록
            User user = userRepository.findById(commentDTO.getUserIdx()).orElse(null);
            comment.setWriter(user);

            // created date 등록
            LocalDateTime current = LocalDateTime.now();
            comment.setCreatedDate(current);

            // Post 등록
            Post post = postRepository.findByPostIdx(commentDTO.getPostIdx());
            comment.setPost(post);

            // DB 등록
            commentRepository.saveAndFlush(comment);

            response.put("success",true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 삭제
    @GetMapping("/delete/{cid}") // commentIdx
    public ResponseEntity deleteComment(@PathVariable("cid")Long commentIdx){

        HashMap<String,Object> response = new HashMap<>();

        try{
            // DB에 is_deleted를 true로 바꿔줌
            commentRepository.deleteComment(commentIdx);

            response.put("success",true);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 목록
    @GetMapping("/getList/{pid}") // postIdx
    public ResponseEntity getCommentList(@PathVariable("pid") Long postIdx){

        HashMap<String,Object> response = new HashMap<>();

        try{
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
