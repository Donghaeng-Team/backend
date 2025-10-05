package com.bytogether.commservice.repository;

import com.bytogether.commservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostIdAndDeletedIsFalse(Long postId);

    List<Post> findByAuthorIdAndDeletedIsFalse(Long userId, Pageable pageable);
}