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
    List<Long> findAllByUserIdx(@Param("userIdx") Long userIdx);                    //해당 회원의 팀 번호를 반환함

    @Query("SELECT DISTINCT ut.user FROM UserTeam ut WHERE ut.team.teamIdx = :teamIdx")
    List<User> findAllUserByTeamIdx(@Param("teamIdx") Long teamIdx);                //해당 팀의 회원_팀를 반환함

    @Query("SELECT ut.user FROM UserTeam ut WHERE ut.team.teamIdx = :teamIdx AND ut.user.userIdx <> :userIdx ORDER BY ut.user.userIdx ASC")
    List<User> findAllUserByTeamIdxAndUserIdx(@Param("teamIdx") Long teamIdx, @Param("userIdx") Long userIdx);        //해당 팀의 회원_팀를 반환함

    Long countUserTeamByTeam_TeamIdxAndUser_UserIdx(Long teamIdx, Long userIdx);    //해당 회원이 팀에 속해있는지 확인 가능(0이면 속함x)
}
