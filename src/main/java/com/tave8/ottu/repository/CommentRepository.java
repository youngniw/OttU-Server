package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Comment;
import com.tave8.ottu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.postIdx = :postIdx AND c.isDeleted=false ORDER BY c.createdDate DESC")
    List<Comment> findAllByPostIdx(@Param("postIdx") Long postIdx);

    Long countAllByPost_PostIdxAndIsDeletedFalse(Long postIdx);    //커뮤니티 글의 댓글 수를 반환함
}
