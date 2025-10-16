package com.bytogether.commservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {  // User-service에서 가져올 데이터
    private Long id;
    private String name;
    private String profileThumbnailImageUrl;



    public static UserDto from(UserInternalResponse userInternalResponse) {
        return new UserDto(
                userInternalResponse.getUserId(),
                userInternalResponse.getNickName(),
                userInternalResponse.getImageUrl()
                );

    }
}
