package com.bytogether.commservice.entity;

import com.bytogether.commservice.util.JsonStringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Where(clause = "deleted=false")
@SQLDelete(sql = "UPDATE posts SET deleted = true WHERE post_id = ?")
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable= false, columnDefinition = "text")
    private String content;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;


    @Convert(converter = JsonStringListConverter.class)
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private List<String> imageUrls;



    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        updateThumbnail();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        updateThumbnail();
    }

    // 썸네일 로직을 처리하는 private 헬퍼 메서드
    private void updateThumbnail() {
        if (this.imageUrls != null && !this.imageUrls.isEmpty()) {
            this.thumbnailUrl = this.imageUrls.get(0);
        } else {
            this.thumbnailUrl = null;
        }
    }




}
