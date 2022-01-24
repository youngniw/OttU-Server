package com.tave8.ottu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long postIdx;       // 글 번호
    private Long userIdx;       // 작성자
    private String content;     // 댓글 내용
}
