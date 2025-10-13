package com.bytogether.userservice.dto.request;

import com.bytogether.userservice.model.VerifyType;
import com.bytogether.userservice.validation.PasswordMatches;
import com.bytogether.userservice.validation.ValidPassword;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
@PasswordMatches
public class ResetPasswordRequest {
    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 값입니다.")
    String email;

    @NotNull(message = "인증 토큰은 필수입니다.")
    String token;

    @NotNull(message = "인증 타입은 필수입니다.")
    VerifyType type;

    @ValidPassword
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    String password;

    @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
    String passwordConfirm;
}
