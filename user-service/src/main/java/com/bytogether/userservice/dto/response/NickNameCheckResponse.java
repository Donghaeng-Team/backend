package com.bytogether.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NickNameCheckResponse {
    boolean duplication;
    private String message;

    public static NickNameCheckResponse unavailable() {
        return NickNameCheckResponse.builder()
                .duplication(true)
                .message("이미 사용중인 닉네임 입니다.")
                .build();
    }

    public static NickNameCheckResponse available() {
        return NickNameCheckResponse.builder()
                .duplication(false)
                .message("사용 가능한 닉네임 입니다.")
                .build();
    }
}
