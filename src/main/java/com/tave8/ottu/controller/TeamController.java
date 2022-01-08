package com.tave8.ottu.controller;

import com.tave8.ottu.dto.OttTeamDTO;
import com.tave8.ottu.dto.SimpleUserDTO;
import com.tave8.ottu.dto.TeamEvaluationDTO;
import com.tave8.ottu.entity.*;
import com.tave8.ottu.service.NoticeService;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/team")
public class TeamController {
    private final TeamService teamService;
    private final RecruitService recruitService;
    private final UserService userService;
    private final NoticeService noticeService;

    @Autowired
    public TeamController(TeamService teamService, RecruitService recruitService, UserService userService, NoticeService noticeService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
        this.userService = userService;
        this.noticeService = noticeService;
    }

    //결제 일자 입력 후 팀 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> postRecruitTeam(@RequestBody OttTeamDTO ottTeamDTO) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Recruit recruit = recruitService.getRecruitById(ottTeamDTO.getRecruitIdx());

            if (!ottTeamDTO.getUserIdx().equals(recruit.getWriter().getUserIdx())) {
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            else if (recruit.getIsTeam()) {
                response.put("success", false);
                response.put("isTeam", true);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            else if (Period.between(recruit.getConfirmedDate().toLocalDate(), LocalDate.now()).getDays() > 7) {
                response.put("success", false);
                response.put("timeout", true);
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            List<Waitlist> recruitAcceptedWaitlist = recruitService.findAcceptedWaitlist(ottTeamDTO.getRecruitIdx());

            Team team = new Team();
            team.setLeader(recruit.getWriter());
            team.setPlatform(recruit.getPlatform());
            team.setHeadcount(recruit.getHeadcount());
            team.setPaymentDay(ottTeamDTO.getPaymentDay());
            team.setCreatedDate(LocalDateTime.now());

            Team savedTeam = teamService.saveTeam(team);

            String content = "'"+recruit.getPlatform().getPlatformName()+"' "+recruit.getHeadcount()+"인 "+recruit.getWriter().getNickname()+"팀이 생성되었습니다.";

            UserTeam leaderTeam = new UserTeam();                       //리더
            leaderTeam.setUser(recruit.getWriter());
            leaderTeam.setTeam(savedTeam);
            teamService.saveUserTeam(leaderTeam);

            Notice notice = new Notice();
            notice.setUser(recruit.getWriter());
            notice.setContent(content);
            noticeService.save(notice);

            for (Waitlist waitlist : recruitAcceptedWaitlist) {         //팀원
                UserTeam userTeam = new UserTeam();
                userTeam.setUser(waitlist.getUser());
                userTeam.setTeam(savedTeam);
                teamService.saveUserTeam(userTeam);

                Notice newNotice = new Notice();
                newNotice.setUser(waitlist.getUser());
                newNotice.setContent(content);
                noticeService.save(newNotice);
            }

            recruitService.deleteNonAcceptedWaitlist(ottTeamDTO.getRecruitIdx());
            recruit.setIsTeam(true);
            recruitService.saveRecruit(recruit);

            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //팀 삭제
    @DeleteMapping("/{tid}")
    public ResponseEntity<Map<String, Object>> deleteRecruit(@PathVariable("tid") Long teamIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Team team = teamService.getTeamById(teamIdx);
            team.setIsDeleted(true);
            if (teamService.saveTeamIsDeleted(team)) {
                List<User> teamUserList = teamService.findAllUserByTeamIdx(teamIdx);
                if (teamUserList.size() == 1) {     //1인 팀
                    Notice notice = new Notice();
                    notice.setUser(teamUserList.get(0));
                    String content = "'"+team.getPlatform().getPlatformName()+"' "+team.getHeadcount()+"인 "+team.getLeader().getNickname()+"팀이 해체되었습니다.";
                    notice.setContent(content);
                    noticeService.save(notice);
                }
                else {
                    for (User user : teamUserList) {
                        Notice notice = new Notice();
                        notice.setUser(user);
                        notice.setEvaluateTeamIdx(teamIdx);
                        String content = "팀원의 해지로 '"+team.getPlatform().getPlatformName()+"' "+team.getLeader().getNickname()+"팀이 해체되었습니다.\n"
                                +"팀원 평가를 진행해 주세요.";
                        notice.setContent(content);
                        noticeService.save(notice);
                    }
                }
                response.put("success", true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{tid}/evaluation/{uid}")
    public ResponseEntity<Map<String, Object>> getTeamToEvaluation(@PathVariable("tid") Long teamIdx, @PathVariable("uid") Long userIdx) {
        HashMap<String,Object> response = new HashMap<>();
        try {
            Optional<Team> team = teamService.findTeamById(teamIdx);
            if (team.isPresent()) {
                List<SimpleUserDTO> simpleUserList = teamService.findSimpleAllUserByTeamIdx(teamIdx, userIdx);

                if (!team.get().getIsDeleted() || simpleUserList.size() < 1) {
                    response.put("success", false);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }

                response.put("success", true);
                response.put("platform", team.get().getPlatform());
                response.put("userlist", simpleUserList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 신뢰도 반영
    @PostMapping("/{tid}/evaluation")
    public ResponseEntity<Map<String, Object>> postTeamReliability(@PathVariable("tid") Long teamIdx, @RequestBody TeamEvaluationDTO teamEvaluationDTO) {
        HashMap<String,Object> response = new HashMap<>();
        try {
            // 팀정보에서 userIdx를 제외한 나머지 user들 가져와야지
            Team team = teamService.findTeamById(teamIdx).orElse(null);
            List<User> userList = teamService.findAllUserByTeamIdxExceptUserIdx(teamIdx, teamEvaluationDTO.getUserIdx());
            List<Integer> evaluationList = teamEvaluationDTO.getReliability();

            // 팀이 해지되었는지 여부 || 평가자가 해당 팀에 포함되어 있는지 여부 || userList는 자신이 포함, evaluationList는 자신이 포함 x
            if (!team.getIsDeleted() || !teamService.isUserInTeam(teamIdx, teamEvaluationDTO.getUserIdx()) || userList.size() != evaluationList.size()){
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

            Optional<Notice> notice = noticeService.findByTeamIdxAndUserIdx(teamIdx, teamEvaluationDTO.getUserIdx());
            notice.get().setIsEvaluated(true);
            noticeService.save(notice.get());

            response.put("success", true);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
