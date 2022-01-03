package com.tave8.ottu.service;

import com.tave8.ottu.entity.User;
import com.tave8.ottu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findUserEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User join(User user) {
        return userRepository.saveAndFlush(user);
    }

    //닉네임 유효 여부(false->이미 존재함  /  true->사용 가능)
    public boolean isExistedNickname(String nickname){
        if (userRepository.findUserByNickname(nickname).isPresent())
            return true;
        else
            return false;
    }

    public boolean isExistedKakaoId(String kakaoId) {
        if (userRepository.findUserByKakaotalkId(kakaoId).isPresent()) {
            return true;
        } else
            return false;
    }

    public void updateUser(Long id,String nickname,String kakaotalkId){
        userRepository.updateUser(id,nickname,kakaotalkId);
    }
}
