package com.bytogether.chatservice.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UsersInfoRequest {
    private List<Long> userIds;

    public static UsersInfoRequest buildRequest(List<Long> userIds) {
        UsersInfoRequest usersInfoRequest = new UsersInfoRequest();

        usersInfoRequest.userIds = userIds;
        return usersInfoRequest;
    }
}
