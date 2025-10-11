package com.bytogether.commservice.service;

import com.bytogether.commservice.dto.CommentCreateRequest;
import com.bytogether.commservice.dto.CommentResponse;
import com.bytogether.commservice.entity.Comment;
import com.bytogether.commservice.entity.Post;
import com.bytogether.commservice.repository.CommentRepository;
import com.bytogether.commservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 고정 페이징: 10개씩, 최신순
    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");


    // 댓글 목록 조회
    public List<CommentResponse> getCommentsByPost(Long postId) {
        Pageable pageable = PageRequest.of(0, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        return commentRepository.findByPost_PostIdOrderByCreatedAtAsc(postId,  pageable)
                .stream()
                .map(this::toResponse).toList();
    }

    // 댓글 작성
    public CommentResponse createComment(Long postId, CommentCreateRequest commentCreateRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .authorId(commentCreateRequest.getUserId())
                .content(commentCreateRequest.getContent())
                .build();

        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    // 댓글 수정
    public CommentResponse updateComment(Long commentId, Long authorId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        comment.setContent(content);
        return toResponse(commentRepository.save(comment));
    }

    // 댓글 삭제 (Soft delete 처리됨)
    public void deleteComment(Long commentId, Long authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);  // @SQLDelete 작동
    }

    // 내부 변환 메서드
    private CommentResponse toResponse(Comment c) {
        return CommentResponse.builder()
                .commentId(c.getCommentId())
                .userId(c.getAuthorId())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
