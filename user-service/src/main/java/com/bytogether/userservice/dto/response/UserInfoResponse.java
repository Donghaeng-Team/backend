package com.bytogether.userservice.dto.response;

import com.bytogether.userservice.model.InitialProvider;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponse {
    String email;
    String nickName;
    String avatarUrl;
    InitialProvider provider;
}
