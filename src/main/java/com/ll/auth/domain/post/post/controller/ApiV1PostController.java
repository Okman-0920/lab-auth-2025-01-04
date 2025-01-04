package com.ll.auth.domain.post.post.controller;

import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.member.member.service.MemberService;
import com.ll.auth.domain.post.post.dto.PostDto;
import com.ll.auth.domain.post.post.entity.Post;
import com.ll.auth.domain.post.post.service.PostService;
import com.ll.auth.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;
    private final MemberService memberService;

    // 다건 조회
    @GetMapping
    public List<PostDto> getItems() {
                return postService
                .findAllByOrderByIdDesc()
                .stream()
                .map(PostDto::new)
                .toList();

/*      List<Post> posts = postService.findAllByOrderByIdDesc();
        List<PostDto> postDtos = new ArrayList<>();
        for (Post post : posts) {
            postDtos.add(new PostDto(post));
        }
        return postDtos;*/// List 사용 시
    }

    // 단건 조회
    @GetMapping("/{id}")
    public PostDto getItem(@PathVariable long id) {
        return postService.findById(id)
                .map(PostDto::new)
                // .map (post -> new PostDto(post))
                .orElseThrow();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public RsData<Void> deleteItem(@PathVariable long id) {
        Post post = postService.findById(id).get();

        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%s번 글이 삭제되었습니다".formatted(id)
        );
    }

    // 수정
    public record PostModifyBody (
            @NotBlank (message = "제목을 입력하세요")
            @Length (min = 2, message = "2자이상 입력하세요")
            String title,

            @NotBlank (message = "내용을 입력하세요")
            @Length (min = 2, message = "2자이상 입력하세요")
            String content
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    public RsData<PostDto> modifyItem(
            @PathVariable long id,
            @RequestBody @Valid PostModifyBody reqBody
    ) {
        Post post = postService.findById(id).get();

        postService.modify(post, reqBody.title(), reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다".formatted(id),
                new PostDto(post)
        );
    }

    // 작성
    public record PostWriteBody (
            @NotBlank
            @Length (min = 2)
            String title,

            @NotBlank
            @Length (min = 2)
            String content,

            @NotNull
            Long authorId
    ) {
    }

    record PostWriteResBody (
        PostDto item,
        long totalCount
    ) {
    }

    @PostMapping
    public RsData<PostWriteResBody> writeItem(
            @RequestBody @Valid PostWriteBody reqBody
    ) {
        Member actor = memberService.findById(reqBody.authorId).get();

        Post post = postService.write(actor, reqBody.title, reqBody.content);

        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다".formatted(post.getId()),
                new PostWriteResBody(
                        new PostDto(post),
                        postService.count()
                        )
                );
    }
}
