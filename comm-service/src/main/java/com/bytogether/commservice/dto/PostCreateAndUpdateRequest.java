package com.bytogether.commservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateAndUpdateRequest {
    private String region;
    private String tag;
    private String title;
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
