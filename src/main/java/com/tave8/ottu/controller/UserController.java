package com.tave8.ottu.controller;

import com.tave8.ottu.dto.UserDTO;
import com.tave8.ottu.entity.Genre;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.User_Genre;
import com.tave8.ottu.repository.UserRepository;
import com.tave8.ottu.repository.User_GenreReposiotry;
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
    private UserRepository userRepository;
    private User_Genre user_genre;
    private User_GenreReposiotry user_genreReposiotry;

    @GetMapping("/join")
    public ResponseEntity joinUser(@RequestBody UserDTO userDTO){

        HashMap<String,Object> response = new HashMap<>();

        try{
            // userDTO에서 받은 userIdx로 user찾기
            User user = userService.findUser(userDTO.getUserIdx()).orElse(null);

            user.setNickname(userDTO.getNickname());
            user.setKakaotalkId(userDTO.getKakaotalkId());
            userService.updateUser(userDTO.getUserIdx(),userDTO.getNickname(),userDTO.getKakaotalkId());

            // 장르 업데이트
            user.setGenres(userDTO.getGenres());

            for(Genre genre:userDTO.getGenres()){
                User_Genre user_genre = new User_Genre(userDTO.getUserIdx(),genre.getGenreIdx());
                user_genreReposiotry.save(user_genre);
            }

            response.put("success",true);
            response.put("User",user);
            return new ResponseEntity(response,HttpStatus.OK);

        }
        catch (Exception e){
            response.put("success",false);
            return new ResponseEntity(response,HttpStatus.INTERNAL_SERVER_ERROR);
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
