package com.bytogether.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvatarCallbackRequest {
    private Long userId;
    private String avatarUrl;
    private boolean success;
}
