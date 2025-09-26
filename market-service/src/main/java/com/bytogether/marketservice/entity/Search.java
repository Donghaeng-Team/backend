package com.bytogether.marketservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 유저 검색 기록을 나타내는 엔티티 클래스입니다.
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Entity
@Table(name = "searches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "division_id", length = 8, nullable = false, updatable = false)
    private String divisionId;

    @Column(name = "depth", nullable = false, updatable = false)
    private Integer depth;

    @Column(name = "category_id", length = 8, updatable = false)
    private String categoryId;

    @Column(name = "keyword", updatable = false)
    private String keyword;

    // Category와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    // 제약조건 검증
    @PrePersist
    @PreUpdate
    private void validateCategoryOrKeyword() {
        if (categoryId == null && keyword == null) {
            throw new IllegalArgumentException("카테고리 또는 키워드 중 하나는 반드시 입력되어야 합니다.");
        }
    }
}