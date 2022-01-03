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
    private PostRepository postRepository;
    private PostService postService;

    // 글 작성
    @PostMapping("/write")
    public ResponseEntity postWrite(@RequestBody PostDTO postDTO){

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
            if(postDTO.getContent() != null){
                post.setContent(postDTO.getContent());
            }
            else{
                new IllegalStateException("내용이 비어있습니다");
                response.put("success",false);
                return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
            }
            // 현재 시간 등록
            LocalDateTime currenttime = LocalDateTime.now();
            post.setCreatedDate(currenttime);
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
    @GetMapping("/list/{pid}")
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

    // 내가 쓴 글 조회
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
    @PostMapping("/edit")
    public ResponseEntity editPost(@RequestBody PostDTO postDTO){

        HashMap<String,Object> response = new HashMap<>();

        try{
            // postIdx정보 가져오기
            Long postIdx = postDTO.getUserIdx();

            //수정 글 가져오기
            String editContent = postDTO.getContent();

            //현재 날짜
            LocalDateTime currentTime = LocalDateTime.now();

            //Post 가져오기
            Post post = postRepository.findByPostIdx(postIdx);
            post.setContent(editContent);
            post.setEditedDate(currentTime);

            //DB에 수정값 넣어주기
            postRepository.updatePost(editContent,postIdx,currentTime);

            response.put("success",true);
            response.put("post",post);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 글 삭제
    @GetMapping("/delete/{pid}")
    public ResponseEntity deletePost(@PathVariable("pid")Long postIdx){

        HashMap<String, Object> response = new HashMap<>();

        try{
            Post post = postService.findPostByPostIdx(postIdx);

            post.setDeleted(true);
            postService.deletePost(true,postIdx);

            response.put("success",true);
            response.put("isDeleted",true);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
