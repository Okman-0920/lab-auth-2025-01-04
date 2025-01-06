package com.ll.auth.domain.post.comment.Controller;

import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.post.comment.dto.PostCommentDto;
import com.ll.auth.domain.post.comment.entity.PostComment;
import com.ll.auth.domain.post.post.entity.Post;
import com.ll.auth.domain.post.post.service.PostService;
import com.ll.auth.global.exceptions.ServiceException;
import com.ll.auth.global.rq.Rq;
import com.ll.auth.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class ApiV1CommentController {
    @Autowired
    @Lazy
    private ApiV1CommentController self;
    private final Rq rq;

    private final PostService postService;

    // 댓글 다건 조회
    @GetMapping
    public List<PostCommentDto> getItems(
            // 양방향 재귀로 인한 오류 발생
            @PathVariable long postId
    ) {
        Post post = postService.findById(postId).orElseThrow(
                // orElseThrow( (Optional == null) -> 예외를 던져라
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        return post
                .getCommentsByOrderByIdDesc()
                .stream()
                .map(PostCommentDto::new)
                // .map(comment -> new PostCommentDto(comment))
                .toList();
    }

    // 댓글 단건 조회
    @GetMapping("{id}")
    public PostCommentDto getItem(
            @PathVariable long postId,
            @PathVariable long id
    ) {
        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        return post
                .getCommentById(id)
                .map(comment -> new PostCommentDto(comment))
                .orElseThrow(
                        () -> new ServiceException("404-2", "%d번 댓글은 존재하지 않습니다.".formatted(id))
                );
    }

    // 댓글 작성
    public record PostCommentWriteBody (
            @NotBlank @Length (min = 2) String content
    ) {
    }

    // 댓글 작성
    @PostMapping
    public RsData<Void> writeItem(
            @PathVariable long postId,
            @RequestBody @Valid PostCommentWriteBody reqBody
    ) {
        PostComment postComment = self._writeItem(postId, reqBody);

        return new RsData<>(
                "201-1",
                "%d번 댓글이 작성되었습니다".formatted(postComment.getId())
        );
    }

    // 댓글 작성
    @Transactional
    public PostComment _writeItem(
            long postId,
            PostCommentWriteBody reqBody
    ) {
        Member actor = rq.checkAuthentication();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("401-1", "%d번 글은 존재하지 않습니다".formatted(postId))
        );

        return post.addComment(
                actor,
                reqBody.content
        );
    }
}
