package com.tave8.ottu.repository;

import com.tave8.ottu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByNickname(String nickname);     //닉네임이 있을 시에는 회원가입이 이미 된 것
    Optional<User> findUserByEmail(String email);
}
