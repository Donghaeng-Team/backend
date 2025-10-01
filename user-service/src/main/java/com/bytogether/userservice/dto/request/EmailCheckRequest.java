package com.bytogether.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor  // JSON 역직렬화를 위해 필요할 수 있음
@Builder
public class EmailCheckRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 값입니다.")
    private String email;

}