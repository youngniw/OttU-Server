package com.tave8.ottu.controller;

import com.tave8.ottu.entity.User;
import com.tave8.ottu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{idx}")
    public ResponseEntity getUser(@PathVariable("idx") Long idx) {      //유저 정보 전달
        Optional<User> user = userService.findUser(idx);

        HashMap<String, Object> response = new HashMap<>();
        if (user.isPresent()) {
            response.put("success", true);
            response.put("user", user);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        else {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity checkExistNickname(@PathVariable("nickname") String nickname) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            boolean isExisted = userService.isExistedNickname(nickname);
            response.put("success", true);
            response.put("isExisted", isExisted);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
