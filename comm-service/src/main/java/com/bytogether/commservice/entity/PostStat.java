package com.bytogether.commservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table( name = "post_stats",
        indexes = {
        @Index(name = "idx_region_createdAt", columnList = "region, createdAt"),
        @Index(name = "idx_region_tag_createdAt", columnList = "region, tag, createdAt")
        })
public class PostStat {
    @Id
    private Long postId; // FK로 Post 연결

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

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

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String thumbnailUrl;

    @Column(nullable = false)
    private boolean deleted;

}