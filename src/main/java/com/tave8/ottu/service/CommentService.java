package com.tave8.ottu.service;

import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // 댓글 가져오기
    public List<Comment> getCommentList(Long postIdx) {
        return commentRepository.findAllByPostIdx(postIdx);
    }

    public Comment getCommentById(Long commentIdx) {
        return commentRepository.getById(commentIdx);
    }

    // 작성글 저장
    public boolean save(Comment comment) {
        try {
            commentRepository.save(comment);
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean saveCommentIsDeleted(Comment comment) {
        try {
            commentRepository.save(comment);
            return true;
        } catch (Exception e) { return false; }
    }
}
