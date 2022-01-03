package com.tave8.ottu.repository;

import com.tave8.ottu.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("SELECT t FROM Team t WHERE t.teamIdx IN (:teamIdxList) AND t.isDeleted=false")
    List<Team> findAllByTeamIdx(@Param("teamIdxList") List<Long> teamIdxList);      //팀 번호로 팀 정보 반환
}
