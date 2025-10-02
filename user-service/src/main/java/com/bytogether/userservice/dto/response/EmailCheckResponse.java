package com.bytogether.userservice.dto.response;

import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCheckResponse {
    private boolean duplication;
    private String message;

    public static EmailCheckResponse unavailable() {
        return EmailCheckResponse.builder()
                .duplication(true)
                .message("가입할수 없는 이메일 입니다.")
                .build();
    }

    public static EmailCheckResponse available() {
        return EmailCheckResponse.builder()
                .duplication(false)
                .message("가입 가능한 이메일입니다.")
                .build();
    }
}
