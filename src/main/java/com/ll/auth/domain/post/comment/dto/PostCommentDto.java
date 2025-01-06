package com.ll.auth.domain.post.comment.dto;

import com.ll.auth.domain.post.comment.entity.PostComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostCommentDto {
    private long id;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private long postId;

    private long authorId;

    private String authorName;

    private String content;

    public PostCommentDto(PostComment postcomment) {
        this.id = postcomment.getId();
        this.createDate = postcomment.getCreateDate();
        this.modifyDate = postcomment.getModifyDate();
        this.postId = postcomment.getPost().getId();
        this.authorId = postcomment.getAuthor().getId();
        this.authorName = postcomment.getAuthor().getName();
        this.content = postcomment.getContent();
    }
}
