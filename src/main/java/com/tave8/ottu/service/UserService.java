package com.tave8.ottu.service;

import com.tave8.ottu.entity.Genre;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.repository.GenreRepository;
import com.tave8.ottu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private GenreRepository genreRepository;

    public Optional<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findUserEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User join(User user) {
        return userRepository.saveAndFlush(user);
    }
    public Genre register(Genre genre) {return genreRepository.saveAndFlush(genre);}

    //닉네임 유효 여부(false->이미 존재함  /  true->사용 가능)
    public boolean isExistedNickname(String nickname){
        if (userRepository.findUserByNickname(nickname).isPresent())
            return true;
        else
            return false;
    }
    public boolean isExistedKakaoId(String kakaotalkId){
        if(userRepository.findUserByKakaotalkId(kakaotalkId).isPresent())
            return true;
        else
            return false;
    }
    @Transactional
    public void changeData(User user){
        User persistance = userRepository.findUserByUserIdx(user.getUserIdx()).orElseThrow(()->{
            return new IllegalArgumentException("회원찾기 실패");
        });
        String nickname = user.getNickname();
        persistance.setNickname(nickname);

        String kakaotalkId = user.getKakaotalkId();
        persistance.setKakaotalkId(kakaotalkId);

        List<Genre> genres = user.getGenres();
        persistance.setGenres(genres);
        // 끝나면 자동 반영
    }






}
