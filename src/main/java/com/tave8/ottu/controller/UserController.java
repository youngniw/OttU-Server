package com.tave8.ottu.controller;

import com.tave8.ottu.dto.UserDTO;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.UserGenre;
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
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/{uid}")
    public ResponseEntity joinUser(@PathVariable("uid") Long userIdx, @RequestBody UserDTO userDTO){
        HashMap<String,Object> response = new HashMap<>();
        try {
            // userDTO에서 받은 userIdx로 user찾기
            User user = userService.getUserById(userIdx);

            user.setNickname(userDTO.getNickname());
            user.setKakaotalkId(userDTO.getKakaotalkId());
            userService.updateUser(userIdx, userDTO.getNickname(), userDTO.getKakaotalkId());

            // 장르 업데이트
            for (Integer genreIdx : userDTO.getGenres()){
                UserGenre userGenre = new UserGenre(userIdx, genreIdx);
                userService.saveUserGenre(userGenre);
            }

            response.put("success",true);
            return new ResponseEntity(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{uid}/notice-token")
    public ResponseEntity patchUserNoticeToken(@PathVariable("uid") Long userIdx, @RequestBody Map<String, String> requestBody) {
        HashMap<String,Object> response = new HashMap<>();
        try {
            User user = userService.getUserById(userIdx);
            if (user.isNoticeTokenNull() != false && user.getNoticeToken().equals(requestBody.get("notice_token"))) {
                response.put("success",true);
                return new ResponseEntity(response,HttpStatus.OK);
            }
            else {
                user.setNoticeToken(requestBody.get("notice_token"));
                if (userService.saveUser(user)) {
                    response.put("success", true);
                    return new ResponseEntity(response,HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{uid}")
    public ResponseEntity getUser(@PathVariable("uid") Long userIdx) {      //유저 정보 전달
        Optional<User> user = userService.findUserById(userIdx);

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
