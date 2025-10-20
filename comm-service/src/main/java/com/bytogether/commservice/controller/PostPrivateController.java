package com.bytogether.commservice.controller;

import com.bytogether.commservice.dto.*;
import com.bytogether.commservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping("/api/v1/posts/private")
@RequiredArgsConstructor
public class PostPrivateController {
    private final PostService postService;

    /**
     * com-04
     * 게시글 초기 작성
     * POST /api/v1/posts/private
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPostInit(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PostCreateAndUpdateRequest request) {
        log.info("게시글 작성 요청 - userId: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(postService.createPostInit(userId, request)));
    }

    /**
     * com-05
     * 게시글 최초 생성 및 수정 (작성자 본인만)
     * PUT /api/v1/posts/private/{postId}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long postId,
            @RequestBody PostCreateAndUpdateRequest request) {
        log.info("게시글 수정 요청 - postId: {}, userId: {}", postId, userId);
        return ResponseEntity.ok(ApiResponse.success(postService.updatePost(userId, postId, request)));
    }

    /**
     * com-06
     * 게시글 삭제 (작성자 본인만)
     * DELETE /api/v1/posts/private/{postId}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long postId) {
        log.info("게시글 삭제 요청 - postId: {}, userId: {}", postId, userId);
        postService.deletePost(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }

    /**
     * com-07
     * 게시글 좋아요 증가
     * POST /api/v1/posts/private/{postId}/likes
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<String>> increaseLike(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long postId) {
        log.info("게시글 좋아요 증가 요청 - postId: {}, userId: {}", postId, userId);
        postService.increaseLikeCount(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 반영되었습니다."));
    }

}
