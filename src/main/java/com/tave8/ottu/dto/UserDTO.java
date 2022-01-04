package com.tave8.ottu.dto;

import com.tave8.ottu.entity.Genre;
import com.tave8.ottu.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    public String success;
    public User user;
    @Data
    public class User{
        public String kakaotalkId;
        public String nickname;
        public List<Genre> genres;
    }
}
