package com.ll.auth.global.rq;

import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.member.member.service.MemberService;
import com.ll.auth.global.exceptions.ServiceException;
import com.ll.auth.global.standard.util.Ut;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

// 이 Class는 Request/ response 를 추상화한 객체
// Request, Response, Cookie, Session 등을 다룬다
@RequestScope
@Component
@RequiredArgsConstructor
public class Rq {
    private final MemberService memberService;
    private final HttpServletRequest request;

    // Authorization 헤더에서 API 키를 꺼내고, 해당 키로 멤버를 인증하는 메서드
    public Member checkAuthentication() {
        String credentials = request.getHeader("Authorization");

        String apiKey = credentials == null?
                ""
                :
                credentials.substring("Bearer ".length());

        if (Ut.str.isBlank(credentials))
            throw new ServiceException("401-1", "apiKey를 입력해주세요.");

        Optional<Member> opActor = memberService.findByApiKey(apiKey);

        if (opActor.isEmpty())
            throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");

        return opActor.get();
    }
}
