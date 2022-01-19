package com.tave8.ottu.controller;
import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.jwt.JwtUtils;
import com.tave8.ottu.service.UserService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/community/comment")
public class CommunityCommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public CommunityCommentController(CommentService commentService, UserService userService, JwtUtils jwtUtils) {
        this.commentService = commentService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    //댓글 작성
    @PostMapping
    private ResponseEntity<Map<String, Object>> writeComment(@RequestBody CommentDTO commentDTO){
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
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{ccid}")   //commentIdx
    public ResponseEntity<Map<String, Object>> deleteComment(HttpServletRequest request, @PathVariable("ccid") Long commentIdx){
        HashMap<String, Object> response = new HashMap<>();
        try {
            String email = (String) jwtUtils.getClaims(request.getHeader("authorization")).get("email");
            Optional<User> user = userService.findUserEmail(email);
            Optional<Comment> comment = commentService.findCommentById(commentIdx);

            if (user.isPresent() && comment.isPresent() && user.get().getUserIdx().equals(comment.get().getWriter().getUserIdx())) {
                comment.get().setIsDeleted(true);
                if (commentService.saveCommentIsDeleted(comment.get())) {
                    response.put("success", true);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else {
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
