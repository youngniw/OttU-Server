package com.tave8.ottu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.service.UserService;
import com.tave8.ottu.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    //카카오 로그인
    @PostMapping("/kakao")
    public ResponseEntity<Map<String, Object>> kakaoLogin(HttpServletRequest request, @RequestBody(required = false) Map<String, String> requestBody) {
        //access_token으로 카카오 로그인함
        if (requestBody != null) {
            String accessToken = requestBody.get("access_token");

            RestTemplate restTemplate = new RestTemplate();  //POST방식으로 데이터를 요청 (카카오 쪽으로)

            HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.add("Authorization","Bearer " + accessToken);
            headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

            HashMap<String, Object> response = new HashMap<>();     //클라이언트에게 보낼 응답
            HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest = new HttpEntity<>(headers);  //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
            ResponseEntity<String> kakaoResponse;
            try {
                kakaoResponse = restTemplate.exchange(    //Post방식으로 Http 요청하기 -> response응답 받음
                        "https://kapi.kakao.com/v2/user/me",
                        HttpMethod.POST,
                        kakaoProfileRequest,
                        String.class
                );
            } catch (Exception e) {     //kakao로부터 받은 응답이 statusCode=200이 아님
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String email;
            try {
                JsonNode userKakaoInfo = objectMapper.readTree(kakaoResponse.getBody());
                JsonNode kakao_account = userKakaoInfo.path("kakao_account");
                email = kakao_account.path("email").asText();
            } catch (JsonProcessingException e) {   //JsonNode를 못읽음
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String jwt = jwtUtils.makeJwtToken(email);
            Optional<User> emailUser = userService.findUserEmail(email);
            if (emailUser.isPresent()) {    //로그인을 해본 클라이언트(-> email이 DB에 포함됨)
                response.put("success", true);
                response.put("jwt", jwt);
                response.put("user", emailUser);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {      //처음 OttU를 사용한 클라이언트
                User newUser = new User();
                newUser.setEmail(email);

                User joinUser = userService.join(newUser);
                response.put("success", true);
                response.put("jwt", jwt);
                response.put("user", joinUser);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        }
        //헤더에 jwt값을 준 경우(클라이언트의 자동로그인)
        else {
            String jwt = request.getHeader("authorization");
            String email = (String) jwtUtils.getClaims(jwt).get("email");

            HashMap<String, Object> response = new HashMap<>();     //클라이언트에게 보낼 응답
            Optional<User> emailUser = userService.findUserEmail(email);
            if (emailUser.isPresent()) {    //로그인을 해본 클라이언트(-> email이 DB에 포함됨)
                response.put("success", true);
                response.put("user", emailUser);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {      //처음 OttU를 사용한 클라이언트
                response.put("success", false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
    }
}
