package com.bytogether.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NicknameCheckRequest {
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이어야 합니다.")
    @NotBlank(message = "닉네임은 필수 항목입니다.")
    private String nickname;
}
