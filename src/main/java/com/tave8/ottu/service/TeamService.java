package com.tave8.ottu.service;

import com.tave8.ottu.entity.Team;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.UserTeam;
import com.tave8.ottu.repository.TeamRepository;
import com.tave8.ottu.repository.UserTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
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

    public Team getTeamById(Long teamIdx) {
        return teamRepository.getById(teamIdx);
    }

    public List<Team> findAllTeamsByUser(Long userIdx) {
        List<Long> teamIdxList = userTeamRepository.findAllByUserIdx(userIdx);

        List<Team> teamList = null;
        if (teamIdxList != null) {
            teamList = teamRepository.findAllByTeamIdx(teamIdxList);
            teamList.forEach(team -> {
                LocalDate today = LocalDate.now();
                int year = today.getYear();
                int month = today.getMonthValue();
                if (today.compareTo(LocalDate.of(year, month, team.getPaymentDay())) <= 0)      //오늘이 더 전이거나 같은 날짜
                    team.setPaymentDate(LocalDate.of(year, month, team.getPaymentDay()));
                else {      //오늘이 더 후 날짜
                    if (month == 12)
                        team.setPaymentDate(LocalDate.of(year+1, 1, team.getPaymentDay()));
                    else
                        team.setPaymentDate(LocalDate.of(year, month+1, team.getPaymentDay()));
                }
            });

            teamList.sort((o1, o2) -> {
                if (o1.getPaymentDate() == null || o2.getPaymentDate() == null)
                    return 0;

                return o1.getPaymentDate().compareTo(o2.getPaymentDate());
            });
        }

        return teamList;
    }

    public List<Team> findAllByPaymentDay(int paymentDay) {
        return teamRepository.findAllByPaymentDay(paymentDay);
    }

    public List<User> findAllUserByTeamIdx(Long teamIdx) {
        return userTeamRepository.findAllUserByTeamIdx(teamIdx);
    }

    public Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    public UserTeam saveUserTeam(UserTeam userTeam) {
        return userTeamRepository.save(userTeam);
    }

    public boolean saveTeamIsDeleted(Team team) {
        try {
            teamRepository.save(team);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Long> findUserIdxByTeamIdx(Long teamIdx){
        return userTeamRepository.findUserIdxByTeamIdx(teamIdx);
    }
}
