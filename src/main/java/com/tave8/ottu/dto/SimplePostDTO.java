package com.tave8.ottu.dto;

import com.tave8.ottu.entity.Platform;
import com.tave8.ottu.entity.Post;
import lombok.Getter;

@Getter
public class SimplePostDTO {
    private Platform platform;
    private String content;

    public SimplePostDTO(Post currentPost) {
        platform = currentPost.getPlatform();
        content = currentPost.getContent();
    }
}
