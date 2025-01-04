package com.ll.auth.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
// null인 필드는 JSON에 포함되지 않음
@AllArgsConstructor
@Getter

// 응답 양식 통일하려고 만든 클래스
public class RsData<T> { // <T> 는 어떤 타입이든 사용 가능하다
    private String resultCode;
    private String msg;
    private T data;

    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("-")[0]);
    }
}
