package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository <Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.platform.platformIdx = :platformIdx AND p.isDeleted = false ORDER BY p.createdDate DESC, p.postIdx DESC")
    List<Post> findAllByPlatformIdx(@Param("platformIdx") int platformIdx);

    //각 플랫폼 마다의 최신글을 받아옴-> 동일 시간이 있다면 post_idx가 높은 것이 조회됨
    @Query(value = "select * " +
            "from (" +
            "select * " +
            "from communitypost " +
            "where (platform_idx, post_idx) in ( " +
            "select platform_idx, max(post_idx) as post_idx " +
            "from communitypost " +
            "where (platform_idx, created_date) in (select platform_idx, max(created_date) as created_date from communitypost where is_deleted=false group by platform_idx) " +
            "group by platform_idx" +
            ")" +
            ") currentpost " +
            "order by platform_idx asc;", nativeQuery = true)
    List<Post> findByCreatedDate();

    @Query("SELECT p FROM Post p WHERE p.writer.userIdx = :userIdx ORDER BY p.createdDate DESC")
    List<Post> findAllByUserIdx(@Param("userIdx") Long userIdx);
}
