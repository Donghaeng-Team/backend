package com.bytogether.chatservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInternalResponse {
    Long userId;
    String nickName;
    String imageUrl;
}
