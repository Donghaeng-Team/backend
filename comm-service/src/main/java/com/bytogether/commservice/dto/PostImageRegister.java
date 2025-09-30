package com.bytogether.commservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 클라이언트가 S3에 업로드 완료 후 서버에 등록할 때 사용하는 이미지 메타 DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImageRegister {

    /** S3 객체 키. 예: posts/2025/09/30/uuid/filename.jpg */
    @NotBlank
    private String s3Key;

    /** 표시 순서. 0부터 시작 권장 */
    @Min(0)
    private Integer order;

    /** 캡션(선택) */
    @Size(max = 200)
    private String caption;

    /** 썸네일로 사용할지 */
    private boolean isThumbnail;

    /** 선택 메타 (클라가 알면 보내고, 모르면 서버가 채워도 됨) */
    @Pattern(regexp = "^image/.+$", message = "이미지는 image/* 만 허용됩니다")
    private String contentType;

    @Positive
    private Long size;

    @Positive
    private Integer width;

    @Positive
    private Integer height;
}