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

    Optional<Comment> findCommentByCommentIdx(Long commentIdx);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Comment c SET c.isDeleted = True WHERE c.commentIdx = :commentIdx",nativeQuery = true)
    int deleteComment(@Param("commentIdx")Long commentIdx);

    @Query("SELECT c FROM Comment c WHERE c.post.postIdx = :postIdx ORDER BY c.createdDate DESC")
    List<Comment> findAllByPostIdx(@Param("postIdx") Long postIdx);
}
