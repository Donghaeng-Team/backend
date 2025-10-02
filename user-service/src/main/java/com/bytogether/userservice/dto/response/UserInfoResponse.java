package com.bytogether.userservice.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponse {
    String email;
    String nickName;
    String avatarUrl;
}
