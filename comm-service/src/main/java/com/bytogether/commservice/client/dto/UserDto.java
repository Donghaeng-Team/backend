package com.bytogether.commservice.client.dto;

import lombok.Data;

@Data
public class UserDto {  // User-service에서 가져올 데이터
    private Long id;
    private String name;
    private String profileThumbnailImageUrl;
}
