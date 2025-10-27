package com.bytogether.chatservice.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserInfoRequest {
    private Long userId;

    public static UserInfoRequest buildRequest(Long userId){
        return UserInfoRequest.builder().userId(userId).build();
    }
}