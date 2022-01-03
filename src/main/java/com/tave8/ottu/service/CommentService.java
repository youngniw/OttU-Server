package com.tave8.ottu.service;

import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 댓글 가져오기
    public List<Comment> getCommentList(Long postIdx){
        List<Comment> commentList = commentRepository.findAllByPostIdx(postIdx);
        List<Comment> newCommentList = new ArrayList<>();

        for(Comment comment : commentList){
            if(comment.getIsDeleted()==false){
                newCommentList.add(comment);
            }
        }

        return newCommentList;
    }
}
