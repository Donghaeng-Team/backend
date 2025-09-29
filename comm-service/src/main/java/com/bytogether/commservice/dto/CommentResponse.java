package com.bytogether.commservice.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String userName;       // 작성자 닉네임
    private String content;
    private LocalDateTime createdAt;
}