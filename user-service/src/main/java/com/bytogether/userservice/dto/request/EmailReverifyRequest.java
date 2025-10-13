package com.bytogether.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailReverifyRequest {
        @Email(message = "이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수 값입니다.")
        private String email;

}

