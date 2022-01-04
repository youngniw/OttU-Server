package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository <Post,Long> {
    @Query("SELECT p FROM Post p WHERE p.platform.platformIdx = :platformIdx AND p.isDeleted = false ORDER BY p.createdDate DESC")
    List<Post> findAllByPlatformIdx(@Param("platformIdx") int platformIdx);

    @Query(value = "SELECT * FROM (" +
            "SELECT * FROM communitypost WHERE (platform_idx, created_date) IN (" +
            "SELECT platform_idx, MAX(created_date) AS created_date " +
            "FROM communitypost GROUP BY platform_idx)" +
            ") post " +
            "GROUP BY post.platform_idx;", nativeQuery = true)
    List<Post> findByCreatedDate();     //각 플랫폼 마다의 최신글을 받아옴

    @Query("SELECT p FROM Post p WHERE p.writer.userIdx = :userIdx ORDER BY p.createdDate DESC")
    List<Post> findAllByUserIdx(@Param("userIdx") Long userIdx);
}
