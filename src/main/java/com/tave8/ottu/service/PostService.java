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
    public boolean save(Post post) {
        try {
            postRepository.save(post);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 플랫폼id로 Post글 가져오기
    public List<Post> findAllByPlatform(int platformIdx) {
        List<Post> postList = postRepository.findAllByPlatformIdx(platformIdx);
        return postList;
    }

    // post idx로 Post 가져오기
    public Post getById(Long postIdx) {
        return postRepository.getById(postIdx);
    }

    // user id로 Post글 가져오기
    public List<Post> findAllByUserIdx(Long userIdx){
        return postRepository.findAllByUserIdx(userIdx);
    }
}
