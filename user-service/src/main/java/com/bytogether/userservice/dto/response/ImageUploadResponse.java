package com.bytogether.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponse {
    private String imageUrl;
}
