package com.tave8.ottu.controller;

import com.tave8.ottu.dto.OttDTO;
import com.tave8.ottu.entity.*;
import com.tave8.ottu.service.PostService;
import com.tave8.ottu.service.RecruitService;
import com.tave8.ottu.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
public class MypageController {
    private final TeamService teamService;
    private final RecruitService recruitService;
    private final PostService postService;

    @Autowired
    public MypageController(TeamService teamService, RecruitService recruitService, PostService postService) {
        this.teamService = teamService;
        this.recruitService = recruitService;
        this.postService = postService;
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

    //내가 쓴 게시글 조회
    @GetMapping("/user/{uid}/post")
    public ResponseEntity getMyPostList(@PathVariable("uid") Long userIdx){
        HashMap<String,Object> response = new HashMap<>();
        try{
            List<Post> myPostList = postService.findAllByUserIdx(userIdx);
            myPostList.forEach(post -> post.setCommentNum(postService.findPostCommentNum(post.getPostIdx())));     //댓글 수 저장

            response.put("success",true);
            response.put("postlist", myPostList);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
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
}
