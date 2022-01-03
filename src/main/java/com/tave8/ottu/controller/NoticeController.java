package com.tave8.ottu.controller;

import com.tave8.ottu.entity.Notice;
import com.tave8.ottu.entity.Team;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.service.NoticeFirebaseMessageService;
import com.tave8.ottu.service.NoticeService;
import com.tave8.ottu.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
public class NoticeController {
    private final TeamService teamService;
    private final NoticeService noticeService;
    private final NoticeFirebaseMessageService noticeFirebaseMessageService;

    @Autowired
    public NoticeController(TeamService teamService, NoticeService noticeService, NoticeFirebaseMessageService noticeFirebaseMessageService) {
        this.teamService = teamService;
        this.noticeService = noticeService;
        this.noticeFirebaseMessageService = noticeFirebaseMessageService;
    }


    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul")
    public void noticePaymentDate() {   //매일 아침 9시에 다음날이 결제일인 사람에게 알림이 가게 함
        List<Team> paymentDayTeamList = teamService.findAllByPaymentDay(LocalDate.now().plusDays(1).getDayOfMonth());

        for (Team team : paymentDayTeamList) {
            List<User> userList = teamService.findAllUserByTeamIdx(team.getTeamIdx());
            for (User user: userList) {
                Notice notice = new Notice();
                notice.setUser(user);
                String content = team.getPlatform().getPlatformName().concat(" 결제일이 하루 남았습니다.");
                notice.setContent(content);
                noticeService.save(notice);

                if (user.getNoticeToken() != null) {
                    try {
                        noticeFirebaseMessageService.sendMessageTo(user.getNoticeToken(), "OTT 서비스 결제일 알림", content);
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }
    }

    @GetMapping("/user/{uid}/notice")
    public ResponseEntity getUserNoticeList(@PathVariable("uid") Long userIdx) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Notice> noticeList = noticeService.findAllByUserIdx(userIdx);

            response.put("success", true);
            response.put("noticelist", noticeList);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
