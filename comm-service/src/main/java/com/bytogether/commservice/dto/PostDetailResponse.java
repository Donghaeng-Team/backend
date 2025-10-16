package com.bytogether.commservice.dto;

import com.bytogether.commservice.client.dto.UserDto;
import com.bytogether.commservice.entity.Post;
import com.bytogether.commservice.entity.PostStat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponse {

    // 📘 Post (본문 데이터)
    private Long postId;
    private String title;
    private String content;
    private String region;
    private String tag;
    private Long authorId;
    private List<String> imageUrls;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto userDto;

    // 📊 PostStat (통계 데이터)
    private long likeCount;
    private long commentCount;
    private long viewCount;

    // ✅ 정적 팩토리 메서드
    public static PostDetailResponse from(Post post, PostStat stat, UserDto userInfo) {
        return PostDetailResponse.builder()
                // Post 필드
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .region(post.getRegion())
                .tag(post.getTag())
                .authorId(post.getAuthorId())
                .imageUrls(post.getImageUrls())
                .thumbnailUrl(post.getThumbnailUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .userDto(userInfo)

                // PostStat 필드
                .likeCount(stat != null ? stat.getLikeCount() : 0)
                .commentCount(stat != null ? stat.getCommentCount() : 0)
                .viewCount(stat != null ? stat.getViewCount() : 0)

                .build();
    }
}
