package com.bytogether.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfoRequest {
    @NotNull
    @NotBlank(message = "이메일은 필수 값입니다.")
    private Long userId;
}