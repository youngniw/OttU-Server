package com.tave8.ottu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/recruit/{platform_id}")
public class RecruitController {
    //모집 관련
    @GetMapping("/")
    public ResponseEntity getRecruitApi() {
        //TODO: 수정 요망!
        HashMap<String, Object> response = new HashMap<>();     //클라이언트에게 보낼 응답
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
