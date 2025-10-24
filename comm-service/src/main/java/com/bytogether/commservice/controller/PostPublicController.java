package com.bytogether.commservice.controller;

import com.bytogether.commservice.dto.ApiResponse;
import com.bytogether.commservice.dto.PostDetailResponse;
import com.bytogether.commservice.dto.PostListResponse;
import com.bytogether.commservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/posts/public")
@RequiredArgsConstructor
public class PostPublicController {
    private final PostService postService;

    /**
     * com-01
     * (지역 구 + 태그별)별 게시글 목록 가져오기
     * GET /api/v1/posts/public?divisionCode={divisionCode}&tag={tag}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getPostsList(
            @RequestParam String divisionCode,
            @RequestParam(required = false, defaultValue = "all") String tag,
            @RequestParam(required = false) String keyword) {
        log.info("게시글 목록 요청 - divisionCode: {}, tag: {}, keyword: {}", divisionCode, tag,keyword);
        return ResponseEntity.ok(ApiResponse.success(postService.getPostsList(divisionCode,tag,keyword)));
    }

    /**
     * com-02
     * 게시글 하나 접근 (상세 조회)
     * GET /api/v1/posts/public/{postId}
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostById(
            @PathVariable Long postId) {
        log.info("게시글 상세 조회 요청 - postId: {}", postId);
        return ResponseEntity.ok(ApiResponse.success(postService.getPostDetail(postId)));
    }

    /**
     * com-03
     * 특정 사용자가 작성한 게시글 목록 조회
     * GET /api/v1/posts/public/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getPostsByUserId(
            @PathVariable Long userId) {
        log.info("사용자 작성 게시글 조회 요청 - userId: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(postService.getPostsByUser(userId)));
    }

    
}
