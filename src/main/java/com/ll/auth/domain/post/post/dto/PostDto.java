package com.ll.auth.domain.post.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ll.auth.domain.post.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

// 프론트 엔지니어와의 소통을 위한 클래스
// 백앤드 개발자(나)가 원래 코드의 형태를 지키기 위함
// 요구사항이 많을 경우, 코드 변수에도 잦은 변경이 일어날 수 있기 때문에
// - 원래 동작하는 코드를 불러오는 형태로 사용할 수 있도록 구조화
@Getter
public class PostDto {
    private long id;

    @JsonProperty("createdDatetime")
    private LocalDateTime createDate;

    @JsonProperty("modifiedDatetime")
    private LocalDateTime modifyDate;

    private long authorId;

    private String authorName;

    private String title;

    private String content;

    public PostDto(Post post) {
        this.id = post.getId();
        this.createDate = post.getCreateDate();
        this.modifyDate = post.getModifyDate();
        this.authorId = post.getAuthor().getId();
        this.authorName = post.getAuthor().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}