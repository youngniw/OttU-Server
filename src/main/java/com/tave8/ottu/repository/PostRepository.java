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
    @Query(value = "SELECT p FROM Communitypost p WHERE p.platform.platformIdx = :platformIdx AND p.isDeleted = false ORDER BY p.createdDate DESC"
,nativeQuery = true)
    List<Post> findAllByPlatformIdx(@Param("platformIdx") int platformIdx);

    @Query(value = "SELECT p FROM Communitypost p WHERE p.user.userIdx = :userIdx ORDER BY p.createdDate DESC",nativeQuery = true)
    List<Post> findAllByUserIdx(@Param("userIdx") Long userIdx);

    @Query(value = "SELECT p FROM Communitypost p WHERE p.postIdx = :postIdx",nativeQuery = true)
    Post findByPostIdx(@Param("postIdx") Long postIdx);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Communitypost p SET p.content = ?1,p.editedDate = ?3 WHERE p.postIdx = ?2",nativeQuery = true)
    int updatePost(String content, Long id, LocalDateTime dt);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Communitypost p SET p.isDeleted = ?1 WHERE p.postIdx = ?2",nativeQuery = true)
    int deletePost(Boolean isDeleted,Long id);
}
