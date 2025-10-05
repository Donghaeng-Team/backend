package com.bytogether.commservice.dto;

import com.bytogether.commservice.entity.Post;
import com.bytogether.commservice.entity.PostStat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponse {

    private Long postId;          // 게시글 ID
    private String title;         // 제목
    private String previewContent; // 내용 요약
    private String region;        // 지역
    private String tag;           // 태그
    private String thumbnailUrl;  // 대표 이미지
    private LocalDateTime createdAt; // 작성일

    private long likeCount;       // 좋아요 수
    private long commentCount;    // 댓글 수
    private long viewCount;       // 조회 수

    public static PostListResponse from(PostStat stat) {
        return PostListResponse.builder()
                .postId(stat.getPostId())
                .region(stat.getRegion())
                .tag(stat.getTag())
                .title(stat.getTitle())
                .previewContent(stat.getPreviewContent())
                .thumbnailUrl(stat.getThumbnailUrl())
                .likeCount(stat.getLikeCount())
                .commentCount(stat.getCommentCount())
                .viewCount(stat.getViewCount())
                .createdAt(stat.getCreatedAt())
                .build();
    }
    public static PostListResponse fromPostEntity(Post post) {
        return PostListResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .region(post.getRegion())
                .tag(post.getTag())
                .thumbnailUrl(post.getThumbnailUrl())
                .previewContent(post.getContent().length() > 100 ? post.getContent().substring(0, 100) + "..." : post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
