package com.ll.auth.domain.post.comment.Controller;

import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.post.comment.dto.PostCommentDto;
import com.ll.auth.domain.post.comment.entity.PostComment;
import com.ll.auth.domain.post.post.entity.Post;
import com.ll.auth.domain.post.post.service.PostService;
import com.ll.auth.global.exceptions.ServiceException;
import com.ll.auth.global.rq.Rq;
import com.ll.auth.global.rsData.RsData;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class ApiV1CommentController {
    private final EntityManager em;
    private final PostService postService;

    private final Rq rq;

    // 댓글 다건 조회
    @GetMapping
    public List<PostCommentDto> getItems(
            // 양방향 재귀로 인한 오류 발생
            @PathVariable long postId
    ) {
        Post post = postService.findById(postId).orElseThrow(
                // orElseThrow( (Optional == null 이면) -> 예외를 던져라
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
    @GetMapping("/{id}")
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
    @Transactional
    public RsData<Void> writeComment(
            @PathVariable long postId,
            @RequestBody @Valid PostCommentWriteBody reqBody
    ) {
        Member actor = rq.checkAuthentication();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("401-1", "%d번 글은 존재하지 않습니다".formatted(postId))
        );

        PostComment postcomment = post.addComment(
                actor,
                reqBody.content
        );

        em.flush();

        return new RsData<>(
                "201-1",
                "%d번 댓글이 작성되었습니다".formatted(postcomment.getId())
        );
    }

    public record PostCommentModifyBody (
            @NotBlank @Length(min = 2) String content
    ) {
    }

    // 댓글 수정
    @PutMapping("/{id}")
    @Transactional
    public RsData<Void> ModifyComment(
            @PathVariable long postId,
            @PathVariable long id,
            @RequestBody @Valid PostCommentModifyBody reqBody
    ) {
        Member actor = rq.checkAuthentication();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다".formatted(postId))
        );

        PostComment postcomment = post.getCommentById(id).orElseThrow(
                () -> new ServiceException("404-2", "%d번 댓글은 존재하지 않습니다".formatted(id))
        );

        if (!postcomment.getAuthor().equals(actor))
            throw new ServiceException("403-1", "작성자만 수정할 수 있습니다.");

        postcomment.modify(reqBody.content);

        return new RsData<>(
                "201-1",
                "%d번 댓글이 수정되었습니다".formatted(postcomment.getId())
        );
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Void> deleteComment(
            @PathVariable long postId,
            @PathVariable long id
    ) {
        Member actor = rq.checkAuthentication();

        Post post = postService.findById(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다".formatted(postId))
        );

        PostComment postcomment = post.getCommentById(id).orElseThrow(
                () -> new ServiceException("404-2", "%d번 댓글은 존재하지 않습니다".formatted(id))
        );

        if (!actor.isAdmin() && !postcomment.getAuthor().equals(actor))
            throw new ServiceException("403-1", "작성자만 삭제할 수 있습니다.");

        post.removeComment(postcomment);

        return new RsData<>(
                "201-1",
                "%d번 댓글이 삭제되었습니다".formatted(id)
        );
    }
}
