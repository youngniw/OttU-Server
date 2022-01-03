package com.tave8.ottu.dto;

import com.tave8.ottu.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userIdx;
    private String kakaotalkId;
    private String nickname;
    private List<Genre> genres;
}
