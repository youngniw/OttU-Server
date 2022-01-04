package com.tave8.ottu.service;

import com.tave8.ottu.entity.Genre;
import com.tave8.ottu.entity.User;
import com.tave8.ottu.entity.UserGenre;
import com.tave8.ottu.repository.GenreRepository;
import com.tave8.ottu.repository.UserGenreRepository;
import com.tave8.ottu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;


    @Autowired
    public UserService(UserRepository userRepository, GenreRepository genreRepository) {
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
    }

    public User getUserById(Long userIdx) {
        return userRepository.getById(userIdx);
    }

    public Optional<User> findUserById(Long userIdx) {
        return userRepository.findById(userIdx);
    }

    public Optional<User> findUserEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User join(User user) {
        return userRepository.saveAndFlush(user);
    }

    //닉네임 유효 여부(false->이미 존재함  /  true->사용 가능)
    public boolean isExistedNickname(String nickname){
        return userRepository.findUserByNickname(nickname).isPresent();
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

    public boolean saveUser(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteUser(User user){
        userRepository.delete(user);
    }

    public Genre findGenreByGenreIDx(int genreIdx){
        return genreRepository.findGenreByGenreIdx(genreIdx).orElse(null);
    }

}
