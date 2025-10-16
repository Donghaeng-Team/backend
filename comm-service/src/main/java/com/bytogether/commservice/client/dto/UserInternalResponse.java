package com.bytogether.commservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInternalResponse {
    private Long userId;
    private String nickName;
    private String imageUrl;
}
