package com.tave8.ottu.controller;

import com.tave8.ottu.entity.Genre;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.repository.GenreRepository;
import com.tave8.ottu.repository.UserRepository;
import com.tave8.ottu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;
    private UserRepository userRepository;
    private GenreRepository genreRepository;

    @GetMapping("/{id}") // id와 jason 형식에서 User부분 가져옴
    public ResponseEntity joinUser(@PathVariable("id") Long idx, @RequestBody(required = false) User requestBody){

        // client에게 보낼 response
        HashMap<String, Object> response = new HashMap<>();

        //idx로 데이터 가져오기!!
        // idx, email, reliability, is_First값은 존재. 나머지 null.
        User user = userRepository.findById(idx).orElse(null);
        // user가 비어있을 경우.
        if(user == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        // requestbody로 받은 kakaotalkId 중복 여부(false -> 계속 진행/ true -> 이미 존재하는 아이디)
        if(userService.isExistedKakaoId(requestBody.getKakaotalkId())==false){
            // requsetbody로 받은 nickname 중복 여부 (false -> 계속 진행/ true -> 이미 존재하는 닉네임)
            if(userService.isExistedNickname(requestBody.getNickname())==false){
                user.setKakaotalkId(requestBody.getKakaotalkId());
                user.setNickname(requestBody.getNickname());
                user.setGenres(requestBody.getGenres());

                // db정보 바꿔주기
                userService.changeData(user);

                response.put("success",true);
                return new ResponseEntity(response,HttpStatus.OK);
            }
            // 이미 존재하는 닉네임
            else{
                new IllegalStateException("중복닉네임입니다");
                response.put("success",false);
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
        // 이미 존재하는 kakaotalkId
        else{
            new IllegalStateException("중복 카카오아이디입니다");
            response.put("success",false);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

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
