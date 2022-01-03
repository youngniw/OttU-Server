package com.tave8.ottu.controller;
import com.tave8.ottu.dto.PostDTO;
import com.tave8.ottu.entity.Platform;
import com.tave8.ottu.entity.Post;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.repository.PostRepository;
import com.tave8.ottu.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/community/post")
public class CommunityPostController {
    @Autowired
    private PostService postService;

    // 글 작성
    @PostMapping("/upload")
    public ResponseEntity postUpload(@RequestBody PostDTO postDTO){
        HashMap<String,Object> response = new HashMap<>();
        try {
            Post post = new Post();
            User user = new User();
            Platform platform = new Platform();

            // 플랫폼 등록
            platform.setPlatformIdx(postDTO.getPlatformIdx());
            post.setPlatform(platform);
            // 작성자 등록
            user.setUserIdx(postDTO.getUserIdx());
            post.setWriter(user);
            // 작성 글 등록
            post.setContent(postDTO.getContent());
            // DB에 저장
            postService.save(post);

            response.put("success",true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
        catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 글 목록 조회(platformIdx로 글 전부 가져오기!!)
    @GetMapping("/{pid}/list")
    public ResponseEntity getPostList(@PathVariable("pid") int platformIdx){
        HashMap<String, Object> response = new HashMap<>();
        try{
            List<Post> postList = postService.findAllByPlatform(platformIdx);

            response.put("success",true);
            response.put("postlist",postList);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 내가 쓴 글 조회        TODO: 주소 변경해야 함!!!!!!(__/user/{uid}/post)
    @GetMapping("/mylist/{uid}")
    public ResponseEntity getMyPostList(@PathVariable("uid") Long userIdx){
        HashMap<String,Object> response = new HashMap<>();
        try{
            List<Post> myPostList = postService.findAllByUserIdx(userIdx);

            response.put("success",true);
            response.put("myPostList",myPostList);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
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

                //DB에 수정값 넣어주기
                postService.save(post);

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
