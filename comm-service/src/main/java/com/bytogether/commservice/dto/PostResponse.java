package com.bytogether.commservice.dto;

import com.bytogether.commservice.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostResponse {

    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String region;
    private String tag;
    private List<String> imageUrls;

    // ⚡ 핵심: Post 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getPostId())
                .authorId(post.getAuthorId())
                .title(post.getTitle())
                .content(post.getContent())
                .region(post.getRegion())
                .tag(post.getTag())
                .imageUrls(post.getImageUrls())
                .build();
    }


}
