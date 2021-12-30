package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findAllByRecruit_RecruitIdx(Long recruitIdx);                //해당 모집글의 참여자 목록을 반환함

    Long countAllByRecruit_RecruitIdxAndIsAcceptedTrue(Long recruitIdx);        //모집글의 참여자 중 선택된 참여자의 수를 반환함
}
