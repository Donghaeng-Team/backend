package com.bytogether.commservice.controller;

import com.bytogether.commservice.dto.CommentCreateRequest;
import com.bytogether.commservice.dto.CommentResponse;
import com.bytogether.commservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/public/{postId}")
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }

    // 댓글 작성
    @PostMapping("/private/{postId}")
    public CommentResponse createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest commentCreateRequest) {
        return commentService.createComment(postId, commentCreateRequest);
    }

    // 댓글 수정
    @PutMapping("/private/{commentId}")
    public CommentResponse updateComment(@PathVariable Long commentId,
                                    @RequestParam Long authorId,
                                    @RequestBody String content) {
        return commentService.updateComment(commentId, authorId, content);
    }

    // 댓글 삭제
    @DeleteMapping("/private/{commentId}")
    public void deleteComment(@PathVariable Long commentId,
                              @RequestParam Long authorId) {
        commentService.deleteComment(commentId, authorId);
    }
}
