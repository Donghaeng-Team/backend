package com.bytogether.marketservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;


/**
 * 공동 구매 이미지 엔티티 클래스 - Image
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
public class Image {

    // 허용되는 MIME 타입
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "market_id", nullable = false)
    private Long marketId;
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder; // 0번은 썸네일
    @Column(name = "original_name", nullable = false)
    private String originalName;
    @Column(name = "stored_name", nullable = false, unique = true)
    private String storedName; // market_id + UUID
    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath; // /dir
    @Column(name = "mime_type", length = 100, nullable = false)
    private String mimeType; // image/png ..etc
    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    // Market과의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", insertable = false, updatable = false)
    private Market market;

    public static Image createImage(Long marketId, Integer sortOrder, String originalName,
                                    String storedName, String filePath, String mimeType) {
        Image image = new Image();
        image.setMarketId(marketId);
        image.setSortOrder(sortOrder);
        image.setOriginalName(originalName);
        image.setStoredName(storedName);
        image.setFilePath("/"+filePath);
        image.setMimeType(mimeType);
        return image;
    }

    // MIME 타입 검증 메서드
    @PrePersist
    @PreUpdate
    private void validateMimeType() {
        if (mimeType != null && !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다: " + mimeType);
        }
        if (sortOrder == null || sortOrder < 0) {
            throw new IllegalArgumentException("정렬 순서는 0 이상의 값이어야 합니다.");
        }
    }

}