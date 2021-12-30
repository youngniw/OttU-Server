package com.tave8.ottu.controller;

import com.tave8.ottu.dto.OttDTO;
import com.tave8.ottu.entity.Platform;
import com.tave8.ottu.entity.Team;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.UserTeam;
import com.tave8.ottu.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class OttController {
    private final TeamService teamService;

    @Autowired
    public OttController(TeamService teamService) {
        this.teamService = teamService;
    }

    //나의 OTT 불러오기
    @GetMapping("/{uid}/ott")
    public ResponseEntity postUploadRecruit(@PathVariable("uid") Long userIdx) {
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

    //나의 OTT 추가하기(=팀 추가)
    @PostMapping("/ott")
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
