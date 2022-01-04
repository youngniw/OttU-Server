package com.tave8.ottu.controller;
import com.tave8.ottu.dto.PostDTO;
import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.entity.Platform;
import com.tave8.ottu.entity.Post;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.service.CommentService;
import com.tave8.ottu.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/community/post")
public class CommunityPostController {
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public CommunityPostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    // 글 작성
    @PostMapping("/upload")
    public ResponseEntity postUpload(@RequestBody PostDTO postDTO){
        HashMap<String,Object> response = new HashMap<>();
        try {
            Post post = new Post();
            User user = new User();
            Platform platform = new Platform();
            platform.setPlatformIdx(postDTO.getPlatformIdx());      // 플랫폼 등록
            post.setPlatform(platform);

            user.setUserIdx(postDTO.getUserIdx());      // 작성자 등록
            post.setWriter(user);

            post.setContent(postDTO.getContent());      // 작성 글 등록

            postService.save(post);

            response.put("success",true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
        catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 글 목록 조회
    @GetMapping("/{pid}/list")
    public ResponseEntity getPostList(@PathVariable("pid") int platformIdx){
        HashMap<String, Object> response = new HashMap<>();
        try{
            List<Post> postList = postService.findAllByPlatform(platformIdx);
            postList.forEach(post -> post.setCommentNum(postService.findPostCommentNum(post.getPostIdx())));     //댓글 수 저장

            response.put("success", true);
            response.put("postlist", postList);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{cpid}")
    public ResponseEntity getPostAndCommentList(@PathVariable("cpid") Long postIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try{
            List<Comment> commentList = new ArrayList<>();

            Optional<Post> post = postService.findById(postIdx);
            if (post.isPresent()) {
                commentList = commentService.getCommentList(postIdx);
                post.get().setCommentNum(Long.valueOf(commentList.size()));     //댓글 수 저장
            }

            response.put("success", true);
            response.put("post", post);
            response.put("commentlist", commentList);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 글 수정
    @PatchMapping("/{cpid}")
    public ResponseEntity editPost(@PathVariable("cpid") Long postIdx, @RequestBody PostDTO postDTO){
        HashMap<String,Object> response = new HashMap<>();
        try {
            //Post 가져오기
            Post post = postService.getById(postIdx);
            if (post.getWriter().getUserIdx().equals(postDTO.getUserIdx())) {
                post.setContent(postDTO.getContent());
                post.setEditedDate(LocalDateTime.now());

                postService.save(post);     //DB에 수정값 넣어주기

                response.put("success", true);
                return new ResponseEntity(response,HttpStatus.OK);
            }
            else {
                response.put("success", false);
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            response.put("success", false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 글 삭제
    @DeleteMapping("/{cpid}")
    public ResponseEntity deletePost(@PathVariable("cpid") Long postIdx){
        HashMap<String, Object> response = new HashMap<>();
        try{
            Post post = postService.getById(postIdx);
            post.setIsDeleted(true);
            if (postService.save(post)) {
                response.put("success", true);
                return new ResponseEntity(response,HttpStatus.OK);
            }
            else {
                response.put("success", false);
                return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        catch (Exception e){
            response.put("success", false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
