package com.tave8.ottu.service;

import com.tave8.ottu.entity.Board;
import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.repository.BoardRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public class BoardService {

    private BoardRepository boardRepository;

    public void write(Board board, String writer, String content, Date now){

        board.setWriter(writer);
        board.setContent(content);
        board.setDate(now);

        boardRepository.saveAndFlush(board);
    }

    @Transactional
    public void updateCommentList(Board board){
        Board persistance = boardRepository.findById(board.getBoardIdx()).orElseThrow(()->{
            return new IllegalArgumentException("회원찾기 실패");
        });

        List<Comment> comments = board.getCommentList();
        persistance.setCommentList(comments);
        // 자동 업데이트
    }
}
