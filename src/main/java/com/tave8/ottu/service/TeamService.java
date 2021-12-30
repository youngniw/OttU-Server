package com.tave8.ottu.service;

import com.tave8.ottu.entity.Team;
import com.tave8.ottu.entity.UserTeam;
import com.tave8.ottu.repository.TeamRepository;
import com.tave8.ottu.repository.UserTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, UserTeamRepository userTeamRepository) {
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
    }

    public List<Team> findAllTeamsByUser(Long userIdx) {
        List<Long> teamIdxList = userTeamRepository.findAllByUserIdx(userIdx);

        List<Team> teamList = null;
        if (teamIdxList != null)
            teamList = teamRepository.findAllByTeamIdx(teamIdxList);

        return teamList;
    }

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    public UserTeam saveUserTeam(UserTeam userTeam) {       //TODO: 안씀(모집 확정 다이얼로그에서 결제일 입력 후 나머지 회원들 추가바람!)
        return userTeamRepository.save(userTeam);
    }
}
