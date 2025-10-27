package com.bytogether.commservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateAndUpdateRequest {
    @NotBlank(message = "지역(region)은 필수입니다.")
    private String region;

    @NotBlank(message = "태그(tag)는 필수입니다.")
    private String tag;

    @NotBlank(message = "제목(title)은 필수입니다.")
    private String title;

    @NotBlank(message = "내용(content)은 필수입니다.")
    private String content;

    @Size(max = 5, message = "이미지는 최대 5개까지 첨부 가능합니다")
    @Builder.Default
    @Valid
    private List<PostImageRegister> images = new ArrayList<>();

    /// 아래 필드는 DB에서 처리
    // private Long postId;
    // private Long userId;
    // private String AuthorName;
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;


}
