package com.bytogether.commservice.service;

import com.bytogether.commservice.client.UserServiceClient;
import com.bytogether.commservice.client.dto.UserDto;
import com.bytogether.commservice.dto.PostCreateRequest;
import com.bytogether.commservice.dto.PostResponse;
import com.bytogether.commservice.entity.Post;
import com.bytogether.commservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        // 글 저장
        Post post = new Post(request.getUserId(), request.getTitle(), request.getContent(), request.getTag());
        postRepository.save(post);

        // 작성자 정보 가져오기 (FeignClient)
        UserDto user = userServiceClient.getUserById(request.getUserId());

        // 응답 조합
        return PostResponse.of(post, user);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없음: " + postId));

        UserDto user = userServiceClient.getUserById(post.getUserId());

        return PostResponse.of(post, user);
    }
}
