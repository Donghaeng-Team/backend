package com.bytogether.commservice.repository;

import com.bytogether.commservice.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostId(Long postId);

    List<Post> findByAuthorId(Long userId, Pageable pageable);

    @Query(value = "select * from posts WHERE post_id= :postId", nativeQuery = true)
    Optional<Post> findByPostIdIncludeTemp(@Param("postId") Long postId);
}