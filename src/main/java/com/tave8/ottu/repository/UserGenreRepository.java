package com.tave8.ottu.repository;

import com.tave8.ottu.entity.UserGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGenreRepository extends JpaRepository<UserGenre,Long> {
    @Query("SELECT u FROM UserGenre u WHERE u.userIdx = :userIdx")
    List<UserGenre> findGenreIdxByUserIdx(@Param("userIdx")Long userIdx);

}
