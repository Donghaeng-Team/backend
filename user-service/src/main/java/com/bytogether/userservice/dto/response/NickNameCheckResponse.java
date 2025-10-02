package com.bytogether.userservice.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NickNameCheckResponse {
    boolean duplication;
    private String message;

    public static NickNameCheckResponse unavailable() {
        return NickNameCheckResponse.builder()
                .duplication(true)
                .message("사용할수 없는 닉네임입니다.")
                .build();
    }

    public static NickNameCheckResponse available() {
        return NickNameCheckResponse.builder()
                .duplication(false)
                .message("사용가능한 닉네임입니다.")
                .build();
    }
}
