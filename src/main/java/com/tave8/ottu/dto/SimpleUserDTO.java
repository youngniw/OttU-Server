package com.tave8.ottu.dto;

import com.tave8.ottu.entity.User;
import lombok.Getter;

@Getter
public class SimpleUserDTO {
    private Long userIdx;
    private String nickname;

    public SimpleUserDTO(User user) {
        userIdx = user.getUserIdx();
        nickname = user.getNickname();
    }
}
