package com.tave8.ottu.service;

import com.tave8.ottu.entity.Post;
import com.tave8.ottu.entity.Recruit;
import com.tave8.ottu.repository.PostRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // 작성글 저장
    public void save(Post post){
        postRepository.save(post);
    }

    // 플랫폼id로 Post글 가져오기
    public List<Post> findAllByPlatform(int platformIdx) {
        List<Post> postList = postRepository.findAllByPlatformIdx(platformIdx);
        return postList;
    }

    // user id로 Post글 가져오기
    public List<Post> findAllByUserIdx(Long userIdx){
        return postRepository.findAllByUserIdx(userIdx);
    }

    // 수정하기
    public void update(String content, Long postId, LocalDateTime dt){
        postRepository.updatePost(content,postId,dt);
    }

    // post idx로 Post 가져오기
    public Post findPostByPostIdx(Long postIdx){
        Post post = postRepository.findByPostIdx(postIdx);
        return post;
    }

    // 삭제하기
    public void deletePost(Boolean isDeleted,Long postIdx){
        postRepository.deletePost(isDeleted,postIdx);
    }

}
