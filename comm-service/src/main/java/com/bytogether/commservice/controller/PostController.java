package com.bytogether.commservice.controller;

import com.bytogether.commservice.dto.PostCreateRequest;
import com.bytogether.commservice.dto.PostResponse;
import com.bytogether.commservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 글 작성
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request) {
        PostResponse response = postService.createPost(request);
        return ResponseEntity.ok(response);
    }

    // 글 단건 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }
}
