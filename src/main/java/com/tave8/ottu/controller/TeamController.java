package com.tave8.ottu.controller;

import com.tave8.ottu.dto.OttTeamDTO;
import com.tave8.ottu.dto.TeamEvaluationDTO;
import com.tave8.ottu.entity.*;
import com.tave8.ottu.service.RecruitService;
import com.tave8.ottu.service.TeamService;
import com.tave8.ottu.service.UserService;
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
    private final UserService userService;

    @Autowired
    public TeamController(TeamService teamService, RecruitService recruitService, UserService userService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
        this.userService = userService;
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

    // 신뢰도 반영
    @PostMapping("/team/{tid}/evaluation")
    public ResponseEntity rateTeamReliability(@PathVariable("tid") Long teamIdx, @RequestBody TeamEvaluationDTO teamEvaluationDTO){
        HashMap<String,Object> response = new HashMap<>();
        try{
            // 팀정보에서 userIdx를 제외한 나머지 user들 가져와야지
            Team team = teamService.findTeamById(teamIdx).orElse(null);
            List<User> userList = teamService.findAllUserByTeamIdxAndUserIdx(teamIdx, teamEvaluationDTO.getUserIdx());
            List<Integer> evaluationList = teamEvaluationDTO.getReliability();

            // 팀이 해지되었는지 여부 || 평가자가 해당 팀에 포함되어 있는지 여부 || userList는 자신이 포함, evaluationList는 자신이 포함 x
            if (!team.getIsDeleted() || !teamService.isUserInTeam(teamIdx, teamEvaluationDTO.getUserIdx()) || userList.size() != evaluationList.size()){
                response.put("success", false);
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }

            for (int i=0; i<userList.size(); i++) {
                User user = userList.get(i);
                // evaluation 가져오기
                Evaluation evaluation = userService.getEvaluation(user.getUserIdx());

                if (evaluation == null) {
                    double newReliability =(double)(10+evaluationList.get(i))/2;

                    Evaluation newEvaluation = new Evaluation();
                    newEvaluation.setUser(user);
                    newEvaluation.setReliability(newReliability);
                    userService.saveEvaluation(newEvaluation);

                    user.setIsFirst(false);
                    user.setReliability((int)(Math.round(newReliability)));
                }
                else {
                    // 현재 거쳐간 회원수
                    int count = evaluation.getCount();
                    // 새로 갱신된 신뢰도
                    double newReliability = (evaluation.getReliability()*count+evaluationList.get(i))/(count+1);

                    evaluation.setCount(count+1);
                    evaluation.setReliability(newReliability);
                    userService.saveEvaluation(evaluation);

                    user.setReliability((int)(Math.round(newReliability)));
                }
                userService.updateUser(user);
            }

            response.put("success", true);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success", false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
