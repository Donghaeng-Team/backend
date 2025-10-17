package com.bytogether.userservice.dto.response;

import com.bytogether.userservice.model.InitialProvider;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
}
