package com.bytogether.commservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_images", indexes = {
        @Index(name = "idx_post_images_post_id", columnList = "post_id"),
        @Index(name = "idx_post_images_order", columnList = "display_order")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 부모 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /** S3 객체 키 */
    @Column(nullable = false, length = 512)
    private String s3Key;

    /** 정렬 순서 */
    @Column(name = "display_order")
    private Integer order;

    /** 썸네일 여부 */
    @Column(nullable = false)
    @Builder.Default
    private boolean thumbnail = false;

    /** 캡션 및 표시용 메타 */
    @Column(length = 200)
    private String caption;

    private String contentType;
    private Long size;
    private Integer width;
    private Integer height;
}