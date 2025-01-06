package com.ll.auth.domain.post.comment.Controller;

import com.ll.auth.domain.post.comment.dto.PostCommentDto;
import com.ll.auth.domain.post.post.entity.Post;
import com.ll.auth.domain.post.post.service.PostService;
import com.ll.auth.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
public class ApiV1CommentController {
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
}
