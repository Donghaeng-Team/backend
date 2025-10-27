package com.bytogether.commservice.entity;

import com.bytogether.commservice.dto.Enum.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "status = 'PUBLISHED'")
@Table( name = "post_stats",
        indexes = {
        @Index(name = "idx_status_region_createdAt", columnList = "status, region, createdAt DESC"),
        @Index(name = "idx_status_region_tag_createdAt", columnList = "status, region, tag, createdAt DESC")

        })
public class PostStat {
    @Id
    private Long postId; // FK로 Post 연결

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private Long userId;  // ✅ 추가: 작성자 ID

    @Column(nullable = false)
    private long viewCount = 0;

    @Column(nullable = false)
    private long commentCount = 0;

    @Column
    private long likeCount = 0;


    //** 캐싱용 필드 **//
    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String previewContent;

    @Column(nullable = false,updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    @Column
    private String thumbnailUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

}