package com.bytogether.commservice.entity;

import com.bytogether.commservice.dto.Enum.PostStatus;
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
@Where(clause = "status = 'PUBLISHED'")
@SQLDelete(sql = "UPDATE posts SET deleted_at = now(), status = 'DELETED' WHERE post_id = ?")
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false,name="author_id")
    private Long authorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable= false, columnDefinition = "text")
    private String content;

    @Column(nullable = false,updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Convert(converter = JsonStringListConverter.class)
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status = PostStatus.TEMP;


    @Builder.Default
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }





}
