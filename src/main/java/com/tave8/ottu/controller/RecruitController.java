package com.tave8.ottu.controller;

import com.tave8.ottu.dto.RecruitDTO;
import com.tave8.ottu.entity.Platform;
import com.tave8.ottu.entity.Recruit;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.Waitlist;
import com.tave8.ottu.service.RecruitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/recruit")
public class RecruitController {
    @Autowired
    private RecruitService recruitService;

    //모집글들 받아오기
    @GetMapping("/list/{pid}")
    public ResponseEntity getRecruitList(@PathVariable("pid") int platformIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Recruit> recruitList = recruitService.findAllByPlatform(platformIdx);
            recruitList.forEach(recruit -> recruit.setChoiceNum(recruitService.findRecruitChoiceNum(recruit.getRecruitIdx())));     //현재 수락된 참여자 수 저장

            response.put("success", true);
            response.put("recruitlist", recruitList);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //내가 쓴 모집글들 받아오기 -> TODO: URL 주소를 변경해야 함!!
    @GetMapping("/my/list/{uid}")
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

    //모집글 작성하기
    @PostMapping("/upload")
    public ResponseEntity postUploadRecruit(@RequestBody RecruitDTO recruitDTO) {
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
            return new ResponseEntity(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 정보 받아오기(사용 x)
    @GetMapping("/{rid}")
    public ResponseEntity getRecruit(@PathVariable("rid") Long recruitIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Recruit> recruit = recruitService.findRecruitById(recruitIdx);
            response.put("success", true);
            response.put("recruit", recruit);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 삭제하기
    @DeleteMapping("/{rid}")
    public ResponseEntity deleteRecruit(@PathVariable("rid") Long recruitIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (recruitService.deleteRecruitById(recruitIdx)) {
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

    //모집글 참여하기
    @PostMapping("/participate")
    public ResponseEntity postParticipate(@RequestBody Map<String, Long> requestBody) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Recruit> recruit = recruitService.findRecruitById(requestBody.get("recruitIdx"));
            if (recruit.get().getIsCompleted() != true) {
                Waitlist waiting = new Waitlist();
                waiting.setRecruit(recruit.get());

                User user = new User();
                user.setUserIdx(requestBody.get("userIdx"));
                waiting.setUser(user);

                boolean participate = recruitService.participate(waiting);

                if (participate) {
                    response.put("success", true);
                    return new ResponseEntity(response, HttpStatus.OK);
                }
            }
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: 모집 확정(-> 참여 요청 다이얼로그에서의 확정!!!!!!!)
    @PostMapping("/confirm")
    public ResponseEntity postRecruitConfirm(@RequestBody Map<String, Long> requestBody) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Long recruitIdx = requestBody.get("recruitIdx");
            Optional<Recruit> recruit = recruitService.findRecruitById(recruitIdx);
            if (Long.valueOf(recruit.get().getHeadcount()).equals(recruitService.findRecruitChoiceNum(recruitIdx))) {
                //TODO: (Team)팀 생성함!    -> 알림을 생성해야 함!!!!!!!!!!!
                response.put("success", true);
                return new ResponseEntity(response, HttpStatus.CREATED);
            }
            else {
                response.put("success", false);
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 참여자 정보 받아오기
    @GetMapping("/waitlist/{rid}")
    public ResponseEntity getRecruitWaitlist(@PathVariable("rid") Long recruitIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Waitlist> recruitWaitlist = recruitService.findRecruitWaitlist(recruitIdx);
            response.put("success", true);
            response.put("applicantNum", recruitWaitlist.size());
            response.put("choiceNum", recruitService.findRecruitChoiceNum(recruitIdx));
            response.put("waitlist", recruitWaitlist);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 참여자 수락
    @PatchMapping("/waitlist/accept")
    public ResponseEntity patchWaitlistAccept(@RequestBody Map<String, Long> requestBody) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Waitlist waiting = recruitService.getWaitlistById(requestBody.get("waitlistIdx"));
            Recruit recruit = waiting.getRecruit();
            if (Long.valueOf(recruit.getHeadcount()).equals(recruitService.findRecruitChoiceNum(recruit.getRecruitIdx()))) {    //이미 정원이 참
                response.put("success", false);
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
            else {      //수락 가능
                waiting.setIsAccepted(true);
                boolean success = recruitService.saveWaitlistIsAccepted(waiting);
                if (success) {
                    response.put("success", true);
                    return new ResponseEntity(response, HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //모집글 참여자 수락 취소
    @PatchMapping("/waitlist/cancel")
    public ResponseEntity patchWaitlistCancel(@RequestBody Map<String, Long> requestBody) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Waitlist waiting = recruitService.getWaitlistById(requestBody.get("waitlistIdx"));

            waiting.setIsAccepted(false);
            boolean success = recruitService.saveWaitlistIsAccepted(waiting);
            if (success) {
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
