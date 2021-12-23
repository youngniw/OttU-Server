package com.tave8.ottu.service;

import com.tave8.ottu.entity.User;
import com.tave8.ottu.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;

    @Test
    void join() throws Exception {
        User user = new User();
        user.setEmail("young@naver.com");

        User joinUser = userService.join(user);
        System.out.println(joinUser.toString());
        Assertions.assertThat(joinUser.getEmail()).isEqualTo("young@naver.com");
        Assertions.assertThat(joinUser.getReliability()).isEqualTo(10);
        Assertions.assertThat(joinUser.getIsFirst()).isEqualTo(true);
    }

    @Test
    void isValidNickname() throws Exception{
        /*
        User writer = new User();
        writer.setUserIdx(1L);
        writer.setNickname("유저1");

         */

        //TODO: 검사해봐야 함!
    }
}