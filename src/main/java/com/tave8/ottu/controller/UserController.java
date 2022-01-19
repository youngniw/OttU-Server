package com.tave8.ottu.controller;

import com.tave8.ottu.dto.UserDTO;
import com.tave8.ottu.entity.Genre;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.jwt.JwtUtils;
import com.tave8.ottu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }


    //사용자 정보 수정
    @PatchMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> joinUser(HttpServletRequest request, @PathVariable("uid") Long userIdx, @RequestBody UserDTO userDTO){
        HashMap<String, Object> response = new HashMap<>();
        try {
            //접근 사용자 조회
            String email = (String) jwtUtils.getClaims(request.getHeader("authorization")).get("email");
            Optional<User> user = userService.findUserEmail(email);

            if (user.isPresent() && user.get().getUserIdx().equals(userIdx)) {
                User changeUser = user.get();

                if (userDTO.getKakaotalkId() != null) {
                    changeUser.setKakaotalkId(userDTO.getKakaotalkId());
                }

                if (userDTO.getNickname() != null) {
                    changeUser.setNickname(userDTO.getNickname());
                }

                if (userDTO.getGenres() != null) {
                    List<Integer> genreList = userDTO.getGenres();
                    List<Genre> genres = new ArrayList<>();
                    for (int genre : genreList) {
                        Genre your_genre = userService.findGenreByGenreIdx(genre);
                        genres.add(your_genre);
                    }
                    changeUser.setGenres(genres);
                }

                userService.updateUser(changeUser);

                response.put("success",true);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }

            else {
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //앱 푸시 알림을 위한 토큰 저장
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

    //사용자 정보 조회
    @GetMapping("/{uid}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable("uid") Long userIdx) {
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
