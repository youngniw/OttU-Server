package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitRepository extends JpaRepository <Recruit, Long> {
    @Query("SELECT r FROM Recruit r WHERE r.platform.platformIdx = :platformIdx ORDER BY r.createdDate DESC")
    List<Recruit> findAllByPlatformIdx(@Param("platformIdx") int platformIdx);  //해당 플랫폼의 모든 글들을 반환함

    @Query("SELECT r FROM Recruit r WHERE r.writer.userIdx = :userIdx ORDER BY r.createdDate DESC")
    List<Recruit> findAllByWriterIdx(@Param("userIdx") Long userIdx);           //writer가 쓴 글들 모두 반환함
}
