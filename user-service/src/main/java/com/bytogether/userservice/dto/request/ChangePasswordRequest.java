package com.bytogether.userservice.dto.request;

import com.bytogether.userservice.validation.PasswordMatches;
import com.bytogether.userservice.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@PasswordMatches
@Getter
@AllArgsConstructor
public class ChangePasswordRequest implements PasswordMatchable{

    @ValidPassword
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String currentPassword;

    @ValidPassword
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    String password;

    @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
    String passwordConfirm;
}
