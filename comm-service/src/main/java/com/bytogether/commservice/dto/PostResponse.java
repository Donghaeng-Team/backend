package com.bytogether.commservice.dto;


import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    private Long postId;        // 글 ID
    private Long userId;        // 작성자 ID
    private String userName;    // 작성자 닉네임 (UserService 연동)
    private String title;
    private String content;
    private List<String> tags;
    private LocalDateTime createdAt;
}