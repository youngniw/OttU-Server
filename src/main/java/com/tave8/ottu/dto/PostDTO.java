package com.tave8.ottu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long postIdx; // 수정에만 사용
    private Long userIdx;
    private int platformIdx;
    private String content;

}
