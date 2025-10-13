package com.bytogether.userservice.dto.response;

import com.bytogether.userservice.model.User;
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
