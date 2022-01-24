package com.tave8.ottu.controller;

import com.tave8.ottu.dto.RecruitDTO;
import com.tave8.ottu.entity.Platform;
import com.tave8.ottu.entity.Recruit;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.Waitlist;
import com.tave8.ottu.jwt.JwtUtils;
import com.tave8.ottu.service.RecruitService;
import com.tave8.ottu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@RestController
@RequestMapping(value = "/recruit")
public class RecruitController {
    private final RecruitService recruitService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public RecruitController(RecruitService recruitService, UserService userService, JwtUtils jwtUtils) {
        this.recruitService = recruitService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    //모집글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getRecruitList(HttpServletRequest request, @RequestParam("pid") int platformIdx, @RequestParam(value = "headcount", required = false) Integer headcount, @RequestParam(value = "completed", required = false) Boolean completed) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            String email = (String) jwtUtils.getClaims(request.getHeader("authorization")).get("email");
            Optional<User> emailUser = userService.findUserEmail(email);

            List<Recruit> recruitList;
            if (headcount != null && completed != null)
                recruitList = recruitService.findAllByPlatformAndHeadcountAndIsCompleted(platformIdx, headcount, completed);
            else if (headcount != null)
                recruitList = recruitService.findAllByPlatformAndHeadcount(platformIdx, headcount);
            else if (completed != null)
                recruitList = recruitService.findAllByPlatformAndIsCompleted(platformIdx, completed);
            else
                recruitList = recruitService.findAllByPlatform(platformIdx);    //필터 없이 전체 모집글 반환

            recruitList.forEach(recruit -> {
                recruit.setChoiceNum(recruitService.findRecruitChoiceNum(recruit.getRecruitIdx()));                 //현재 수락된 참여자 수 저장
                recruit.setIsApplying(recruitService.findByUserApplyingRecruit(recruit.getRecruitIdx(), emailUser.get().getUserIdx()));  //회원이 참여요청 했는 지 확인 가능
            });

            response.put("success", true);
            response.put("recruitlist", recruitList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 추가
    @PostMapping
    public ResponseEntity<Map<String, Object>> postUploadRecruit(@RequestBody RecruitDTO recruitDTO) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Recruit recruit = new Recruit();

            Platform platform = new Platform();
            platform.setPlatformIdx(recruitDTO.getPlatformIdx());
            recruit.setPlatform(platform);

            User writer = new User();
            writer.setUserIdx(recruitDTO.getUserIdx());
            recruit.setWriter(writer);

            recruit.setHeadcount(recruitDTO.getHeadcount());

            if (recruitDTO.getHeadcount() == 1)
                recruit.setIsCompleted(true);   //정원이 1명이면 이미 모집이 된 것임!

            recruitService.upload(recruit);

            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 정보 조회
    @GetMapping("/{rid}")
    public ResponseEntity<Map<String, Object>> getRecruit(@PathVariable("rid") Long recruitIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Recruit> recruit = recruitService.findRecruitById(recruitIdx);
            response.put("success", true);
            response.put("recruit", recruit);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 삭제
    @DeleteMapping("/{rid}")
    public ResponseEntity<Map<String, Object>> deleteRecruit(HttpServletRequest request, @PathVariable("rid") Long recruitIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            String email = (String) jwtUtils.getClaims(request.getHeader("authorization")).get("email");
            Optional<User> emailUser = userService.findUserEmail(email);
            Optional<Recruit> recruit = recruitService.findRecruitById(recruitIdx);

            if (emailUser.isPresent() && recruit.isPresent() && emailUser.get().getUserIdx().equals(recruit.get().getWriter().getUserIdx())) {
                if (recruitService.deleteRecruitById(recruitIdx)) {
                    response.put("success", true);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else {
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 참여
    @PostMapping("/participate")
    public ResponseEntity<Map<String, Object>> postParticipate(@RequestBody Map<String, Long> requestBody) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Long recruitIdx = requestBody.get("recruitIdx");
            Long userIdx = requestBody.get("userIdx");
            Optional<Recruit> recruit = recruitService.findRecruitById(recruitIdx);
            if (!recruit.get().getIsCompleted() || !recruitService.findByUserApplyingRecruit(recruitIdx, userIdx)) {
                Waitlist waiting = new Waitlist();
                waiting.setRecruit(recruit.get());

                User user = new User();
                user.setUserIdx(userIdx);
                waiting.setUser(user);

                boolean participate = recruitService.participate(waiting);

                if (participate) {
                    response.put("success", true);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집 확정 - 팀원 정보 전달
    @GetMapping("/{rid}/members")
    public ResponseEntity<Map<String, Object>> getRecruitMembers(@PathVariable("rid") Long recruitIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Recruit recruit = recruitService.getRecruitById(recruitIdx);
            if (Long.valueOf(recruit.getHeadcount()).equals(recruitService.findRecruitChoiceNum(recruitIdx))) {         //모집 인원 수와 수락 인원 수가 같음
                List<Waitlist> recruitAcceptedWaitlist = recruitService.findAcceptedWaitlist(recruitIdx);
                boolean success = true;
                if (recruit.getConfirmedDate() == null) {
                    recruit.setIsCompleted(true);
                    recruit.setConfirmedDate(LocalDateTime.now());      //모집 확정 날짜 설정

                    success = recruitService.saveRecruit(recruit);
                }

                if (success) {
                    response.put("success", true);
                    response.put("members", recruitAcceptedWaitlist);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else {
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 참여자 정보 조회
    @GetMapping("/{rid}/waitlist")
    public ResponseEntity<Map<String, Object>> getRecruitWaitlist(@PathVariable("rid") Long recruitIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Recruit recruit = recruitService.getRecruitById(recruitIdx);
            List<Waitlist> recruitWaitlist = recruitService.findRecruitWaitlist(recruitIdx);

            response.put("success", true);
            response.put("applicantNum", recruitWaitlist.size());
            response.put("choiceNum", recruitService.findRecruitChoiceNum(recruitIdx));

            if (recruit.getConfirmedDate() == null || Period.between(recruit.getConfirmedDate().toLocalDate(), LocalDate.now()).getDays() <= 7)
                response.put("timeout", false);
            else
                response.put("timeout", true);

            if (recruit.getIsTeam())
                response.put("isTeam", true);
            else
                response.put("isTeam", false);

            response.put("waitlist", recruitWaitlist);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 참여자 수락
    @PatchMapping("/waitlist/accept")
    public ResponseEntity<Map<String, Object>> patchWaitlistAccept(@RequestBody Map<String, Long> requestBody) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Waitlist waiting = recruitService.getWaitlistById(requestBody.get("waitlistIdx"));
            Recruit recruit = waiting.getRecruit();
            if (recruit.getIsCompleted()) {                 //이미 모집이 확정되었다면 수락이 더이상 되지 않음
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            else if (Long.valueOf(recruit.getHeadcount()).equals(recruitService.findRecruitChoiceNum(recruit.getRecruitIdx()))) {    //이미 정원이 참
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            else {      //수락 가능
                waiting.setIsAccepted(true);
                boolean success = recruitService.saveWaitlistIsAccepted(waiting);
                if (success) {
                    response.put("success", true);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 참여자 수락 취소
    @PatchMapping("/waitlist/cancel")
    public ResponseEntity<Map<String, Object>> patchWaitlistCancel(@RequestBody Map<String, Long> requestBody) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Waitlist waiting = recruitService.getWaitlistById(requestBody.get("waitlistIdx"));

            if (waiting.getRecruit().getIsCompleted()) {    //이미 모집이 확정되었다면 수락취소가 더이상 되지 않음
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            else {
                waiting.setIsAccepted(false);
                boolean success = recruitService.saveWaitlistIsAccepted(waiting);
                if (success) {
                    response.put("success", true);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
