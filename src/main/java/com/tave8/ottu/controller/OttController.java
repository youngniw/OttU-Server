package com.tave8.ottu.controller;

import com.tave8.ottu.dto.OttDTO;
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
public class OttController {
    private final TeamService teamService;
    private final RecruitService recruitService;

    @Autowired
    public OttController(TeamService teamService, RecruitService recruitService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
    }

    //내가 쓴 모집글들 받아오기
    @GetMapping("/user/{uid}/recruit")
    public ResponseEntity getUserRecruitList(@PathVariable("uid") Long userIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Recruit> recruitList = recruitService.findAllByWriter(userIdx);
            recruitList.forEach(recruit -> recruit.setChoiceNum(recruitService.findRecruitChoiceNum(recruit.getRecruitIdx())));     //현재 수락된 참여자 수 저장

            response.put("success", true);
            response.put("recruitlist", recruitList);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //나의 OTT 불러오기
    @GetMapping("/user/{uid}/ott")
    public ResponseEntity getUserOttList(@PathVariable("uid") Long userIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Team> ottList = teamService.findAllTeamsByUser(userIdx);

            response.put("success", true);
            response.put("ottlist", ottList);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //2주내 결제 예정인 나의 OTT 불러오기
    @GetMapping("/user/{uid}/urgent-ott")
    public ResponseEntity getUserUrgentOttList(@PathVariable("uid") Long userIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Team> ottList = teamService.findAllTeamsByUser(userIdx);

            response.put("success", true);
            if (ottList.size() > 0) {
                LocalDate today = LocalDate.now();
                LocalDate inTwoWeeks = today.plusWeeks(2);

                int notUrgentPos = ottList.size();
                for (int i=0; i<ottList.size(); i++) {
                    if (ottList.get(i).getPaymentDate().compareTo(inTwoWeeks) > 0) {
                        notUrgentPos = i;
                        break;
                    }
                }
                response.put("ottlist", ottList.subList(0, notUrgentPos));
            }
            else {
                response.put("ottlist", ottList);
            }
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //나의 OTT 추가하기(=팀 추가)
    @PostMapping("/user/ott")
    public ResponseEntity postUploadRecruit(@RequestBody OttDTO ottDTO) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Team team = new Team();

            User user = new User();
            user.setUserIdx(ottDTO.getUserIdx());
            team.setLeader(user);

            Platform platform = new Platform();
            platform.setPlatformIdx(ottDTO.getPlatformIdx());
            team.setPlatform(platform);

            team.setHeadcount(ottDTO.getHeadcount());
            team.setPaymentDay(ottDTO.getPaymentDay());

            Team savedTeam = teamService.saveTeam(team);

            UserTeam userTeam = new UserTeam();
            userTeam.setUser(user);
            userTeam.setTeam(savedTeam);
            teamService.saveUserTeam(userTeam);

            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
