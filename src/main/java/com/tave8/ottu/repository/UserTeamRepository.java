package com.tave8.ottu.repository;

import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
    @Query("SELECT DISTINCT ut.team.teamIdx FROM UserTeam ut WHERE ut.user.userIdx = :userIdx")
    List<Long> findAllByUserIdx(@Param("userIdx") Long userIdx);            //해당 회원의 팀 번호를 반환함

    @Query("SELECT DISTINCT ut.user FROM UserTeam ut WHERE ut.team.teamIdx = :teamIdx")
    List<User> findAllUserByTeamIdx(@Param("teamIdx") Long teamIdx);    //해당 팀의 회원_팀를 반환함
}
