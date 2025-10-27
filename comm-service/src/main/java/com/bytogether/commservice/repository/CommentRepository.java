package com.bytogether.commservice.repository;

import com.bytogether.commservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost_PostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);
}