package com.tave8.ottu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String comment; // 댓글 내용
    private Long CommentIdx; // 댓글번호
    private Long PostIdx; // 글 번호
    private Long UserIdx; // 작성자
}
