package com.bytogether.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailCheckResponse {
    private boolean duplication;
    private String message;

    public static EmailCheckResponse unavailable() {
        return EmailCheckResponse.builder()
                .duplication(true)
                .message("이미 사용중인 이메일 입니다.")
                .build();
    }

    public static EmailCheckResponse available() {
        return EmailCheckResponse.builder()
                .duplication(false)
                .message("사용 가능한 이메일입니다.")
                .build();
    }
}
