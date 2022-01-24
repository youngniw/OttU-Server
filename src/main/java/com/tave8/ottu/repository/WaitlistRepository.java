package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    @Query("SELECT w FROM Waitlist w WHERE w.recruit.recruitIdx = :recruitIdx ORDER BY w.isAccepted DESC")
    List<Waitlist> findAllByRecruitIdxOrderByIsAcceptedDesc(@Param("recruitIdx") Long recruitIdx);  //해당 모집글의 참여자 목록을 반환함(수락 순)

    @Query("SELECT w FROM Waitlist w WHERE w.recruit.recruitIdx = :recruitIdx AND w.isAccepted = true")
    List<Waitlist> findAllByRecruitIdxAndIsAcceptedTrue(@Param("recruitIdx") Long recruitIdx);      //해당 모집글의 참여자 중 수락된 참여자 목록을 반환함

    Long countAllByRecruit_RecruitIdxAndIsAcceptedTrue(Long recruitIdx);                            //모집글의 참여자 중 선택된 참여자의 수를 반환함

    Long countWaitlistByRecruit_RecruitIdxAndUser_UserIdx(Long recruitIdx, Long userIdx);           //해당 회원이 대기자에 속해있는지 확인 가능(0이면 속함x)

    @Query("DELETE FROM Waitlist w WHERE w.recruit.recruitIdx = :recruitIdx AND w.isAccepted = false")
    void deleteAllByRecruitIdxAndIsAcceptedFalse(@Param("recruitIdx") Long recruitIdx);             //해당 모집글의 참여자 중 수락되지 않은 참여자 목록 삭제
}
