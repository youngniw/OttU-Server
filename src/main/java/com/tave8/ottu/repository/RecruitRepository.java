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

    //필터 - 인원수에 따른 모집글들
    @Query("SELECT r FROM Recruit r WHERE r.platform.platformIdx = :platformIdx AND r.headcount = :headcount ORDER BY r.createdDate DESC")
    List<Recruit> findAllByPlatformAndHeadcount(@Param("platformIdx") int platformIdx,  @Param("headcount") int headcount);

    //필터 - 모집완료 여부에 따른 모집글
    @Query("SELECT r FROM Recruit r WHERE r.platform.platformIdx = :platformIdx AND r.isCompleted = :isCompleted ORDER BY r.createdDate DESC")
    List<Recruit> findAllByPlatformAndIsCompleted(@Param("platformIdx") int platformIdx,  @Param("isCompleted") boolean isCompleted);

    //필터 - 인원 수와 모집완료 여부에 따른 모집글
    @Query("SELECT r FROM Recruit r WHERE r.platform.platformIdx = :platformIdx AND r.headcount = :headcount AND r.isCompleted = :isCompleted ORDER BY r.createdDate DESC")
    List<Recruit> findAllByPlatformAndHeadcountAndIsCompleted(@Param("platformIdx") int platformIdx, @Param("headcount") int headcount, @Param("isCompleted") boolean isCompleted);

    @Query("SELECT r FROM Recruit r WHERE r.writer.userIdx = :userIdx ORDER BY r.createdDate DESC")
    List<Recruit> findAllByWriterIdx(@Param("userIdx") Long userIdx);           //writer가 쓴 글들 모두 반환함
}
