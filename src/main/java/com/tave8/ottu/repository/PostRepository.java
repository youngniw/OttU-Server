package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Post;
import com.tave8.ottu.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository <Post,Long> {
    @Query("SELECT p FROM Post p WHERE p.platform.platformIdx = :platformIdx AND p.isDeleted = false ORDER BY p.createdDate DESC")
    List<Post> findAllByPlatformIdx(@Param("platformIdx") int platformIdx);

    @Query("SELECT p FROM Post p WHERE p.writer.userIdx = :userIdx ORDER BY p.createdDate DESC")
    List<Post> findAllByUserIdx(@Param("userIdx") Long userIdx);
}
