package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("SELECT n FROM Notice n WHERE n.user.userIdx = :userIdx ORDER BY n.createdDate DESC")
    List<Notice> findAllByUserIdx(@Param("userIdx") Long userIdx);       //해당 회원의 알림을 최신순으로 모두 반환함

    @Query("SELECT n FROM Notice n WHERE n.evaluateTeamIdx = :evaluateTeamIdx AND n.user.userIdx = :userIdx")
    Optional<Notice> findByUserIdxAndTeamIdx(@Param("evaluateTeamIdx") Long evaluateTeamIdx, @Param("userIdx") Long userIdx);
}
