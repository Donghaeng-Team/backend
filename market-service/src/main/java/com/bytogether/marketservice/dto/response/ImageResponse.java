package com.bytogether.marketservice.dto.response;

import com.bytogether.marketservice.entity.Image;
import lombok.*;

/**
 * 공동 구매 마켓 이미지 응답 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-01
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ImageResponse {
    private Integer sortOrder; // 이미지 순서
    private String imageUrl; // 이미지 URL
    private String originalName; // 원본 이미지 이름

    public static ImageResponse fromEntity(Image image) {
        if (image == null) {
            return null;
        }
        return ImageResponse.builder()
                .sortOrder(image.getSortOrder())
                .imageUrl(image.getFilePath())
                .originalName(image.getOriginalName())
                .build();
    }
}
