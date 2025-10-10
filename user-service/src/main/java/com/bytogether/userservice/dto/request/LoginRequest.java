package com.bytogether.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "로그인 시 이메일은 필수 정보입니다.")
    private String email;
    @NotBlank(message = "로그인 시 비밀번호는 필수 정보입니다.")
    private String password;
}