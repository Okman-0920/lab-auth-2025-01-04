package com.ll.auth.domain.post.post.entity;

import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.post.comment.entity.PostComment;
import com.ll.auth.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(length = 30)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<PostComment> comments = new ArrayList<>();

    public void addComment(Member author, String content) {
        PostComment postComment = PostComment.builder()
                .post(this)
                .author(author)
                .content(content)
                .build();

        comments.add(postComment);
    }
}
