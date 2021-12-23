package com.tave8.ottu.controller;

import com.tave8.ottu.entity.Recruit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/recruit")
public class RecruitController {
    //모집 관련
    @GetMapping("/")
    public ArrayList<Recruit> getRecruitApi() {
        //TODO: 수정 요망!
        return null;
    }
}
