package com.bytogether.commservice.dto;

import java.util.List;

public class PostCreateRequest {
    private Long userId;        // 작성자 식별자
    private String title;       // 글 제목
    private String content;     // 글 내용 (본문)
    private List<String> tags;  // 태그 목록 (옵션)
}
