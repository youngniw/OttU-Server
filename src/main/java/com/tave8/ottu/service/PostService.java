package com.tave8.ottu.service;

import com.tave8.ottu.dto.SimplePostDTO;
import com.tave8.ottu.entity.Post;
import com.tave8.ottu.repository.CommentRepository;
import com.tave8.ottu.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    // 작성글 저장
    public boolean save(Post post) {
        try {
            postRepository.save(post);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //플랫폼 id로 Post글 가져오기
    public List<Post> findAllByPlatform(int platformIdx) {
        return postRepository.findAllByPlatformIdx(platformIdx);
    }

    public List<SimplePostDTO> findCurrentPlatformPost() {
        List<Post> postList = postRepository.findByCreatedDate();
        return postList.stream().map(SimplePostDTO::new).collect(Collectors.toList());
    }

    public Optional<Post> findById(Long postIdx) {
        return postRepository.findById(postIdx);
    }

    // post idx로 Post 가져오기
    public Post getById(Long postIdx) {
        return postRepository.getById(postIdx);
    }

    // user id로 Post글 가져오기
    public List<Post> findAllByUserIdx(Long userIdx){
        return postRepository.findAllByUserIdx(userIdx);
    }

    public Long findPostCommentNum(Long postIdx) {
        return commentRepository.countAllByPost_PostIdxAndIsDeletedFalse(postIdx);
    }
}
