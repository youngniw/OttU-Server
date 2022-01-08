package com.tave8.ottu.controller;

import com.tave8.ottu.dto.UserDTO;
import com.tave8.ottu.entity.Genre;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PatchMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> joinUser(@PathVariable("uid") Long userIdx, @RequestBody UserDTO userDTO){
        HashMap<String, Object> response = new HashMap<>();
        try {
            // userDTO에서 받은 userIdx로 user찾기
            User user = userService.findUserById(userIdx).orElse(null);

            if (userDTO.getKakaotalkId() != null) {
                user.setKakaotalkId(userDTO.getKakaotalkId());
            }

            if (userDTO.getNickname() != null) {
                user.setNickname(userDTO.getNickname());
            }

            if (userDTO.getGenres() != null) {
                List<Integer> genreList = userDTO.getGenres();
                List<Genre> genres = new ArrayList<>();
                for (int genre : genreList) {
                    Genre your_genre = userService.findGenreByGenreIdx(genre);
                    genres.add(your_genre);
                }
                user.setGenres(genres);
            }

            userService.updateUser(user);

            response.put("success",true);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{uid}/notice-token")
    public ResponseEntity<Map<String, Object>> patchUserNoticeToken(@PathVariable("uid") Long userIdx, @RequestBody Map<String, String> requestBody) {
        HashMap<String, Object> response = new HashMap<>();     //안드로이드에서 사용되는 noticeToken
        try {
            User user = userService.getUserById(userIdx);
            if (!user.isNoticeTokenNull() && user.getNoticeToken().equals(requestBody.get("notice_token"))) {   //기존에 저장된 token과 동일할 시
                response.put("success", true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                user.setNoticeToken(requestBody.get("notice_token"));
                if (userService.saveUser(user)) {
                    response.put("success", true);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
                else {
                    response.put("success", false);
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable("uid") Long userIdx) {      //유저 정보 전달
        Optional<User> user = userService.findUserById(userIdx);

        HashMap<String, Object> response = new HashMap<>();
        if (user.isPresent()) {
            response.put("success", true);
            response.put("user", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Map<String, Object>> checkExistNickname(@PathVariable("nickname") String nickname) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            boolean isExisted = userService.isExistedNickname(nickname);
            response.put("success", true);
            response.put("isExisted", isExisted);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("uid") Long userIdx){

        HashMap<String,Object> response = new HashMap<>();

        try{
            User user = userService.findUserById(userIdx).orElse(null);
            if(user == null){
                response.put("success",false);
                return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
            userService.deleteUser(user);

            response.put("success",true);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
