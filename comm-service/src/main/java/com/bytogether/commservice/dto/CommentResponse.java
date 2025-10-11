package com.bytogether.commservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String userName;       // 작성자 닉네임
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}