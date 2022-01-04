package com.tave8.ottu.controller;

import com.tave8.ottu.dto.OttTeamDTO;
import com.tave8.ottu.entity.*;
import com.tave8.ottu.service.RecruitService;
import com.tave8.ottu.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;

@RestController
public class TeamController {
    private final TeamService teamService;
    private final RecruitService recruitService;

    @Autowired
    public TeamController(TeamService teamService, RecruitService recruitService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
    }

    //결제 일자 입력 후 팀 생성
    @PostMapping("/team")
    public ResponseEntity postRecruitTeam(@RequestBody OttTeamDTO ottTeamDTO) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Recruit recruit = recruitService.getRecruitById(ottTeamDTO.getRecruitIdx());

            if (!ottTeamDTO.getUserIdx().equals(recruit.getWriter().getUserIdx())) {
                response.put("success", false);
                return new ResponseEntity(response, HttpStatus.UNAUTHORIZED);
            }
            else if (recruit.getIsTeam() == true) {
                response.put("success", false);
                response.put("isTeam", true);
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
            else if (Period.between(recruit.getConfirmedDate().toLocalDate(), LocalDate.now()).getDays() > 7) {
                response.put("success", false);
                response.put("timeout", true);
                return new ResponseEntity(response, HttpStatus.FORBIDDEN);
            }

            List<Waitlist> recruitAcceptedWaitlist = recruitService.findAcceptedWaitlist(ottTeamDTO.getRecruitIdx());

            Team team = new Team();
            team.setLeader(recruit.getWriter());
            team.setPlatform(recruit.getPlatform());
            team.setHeadcount(recruit.getHeadcount());
            team.setPaymentDay(ottTeamDTO.getPaymentDay());
            team.setCreatedDate(LocalDateTime.now());

            Team savedTeam = teamService.saveTeam(team);

            UserTeam leaderTeam = new UserTeam();                       //리더
            leaderTeam.setUser(recruit.getWriter());
            leaderTeam.setTeam(savedTeam);
            teamService.saveUserTeam(leaderTeam);
            for (Waitlist waitlist : recruitAcceptedWaitlist) {         //팀원
                UserTeam userTeam = new UserTeam();
                userTeam.setUser(waitlist.getUser());
                userTeam.setTeam(savedTeam);
                teamService.saveUserTeam(userTeam);
            }

            recruitService.deleteNonAcceptedWaitlist(ottTeamDTO.getRecruitIdx());
            recruit.setIsTeam(true);
            recruitService.saveRecruit(recruit);

            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //팀 삭제
    @DeleteMapping("/team/{tid}")
    public ResponseEntity deleteRecruit(@PathVariable("tid") Long teamIdx) {
        //TODO: 팀원 평가가 이루어지게 해야 함!
        HashMap<String, Object> response = new HashMap<>();
        try {
            Team team = teamService.getTeamById(teamIdx);
            team.setIsDeleted(true);
            if (teamService.saveTeamIsDeleted(team)) {
                response.put("success", true);
                return new ResponseEntity(response, HttpStatus.OK);
            }
            else {
                response.put("success", false);
                return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
