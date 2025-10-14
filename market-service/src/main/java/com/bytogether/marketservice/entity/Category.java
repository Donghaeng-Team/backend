package com.bytogether.marketservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 공동 구매 카테고리 엔티티 클래스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @Column(name = "id", length = 8, nullable = false, updatable = false, insertable = false)
    private String id;

    @Column(name = "name", length = 50, nullable = false, updatable = false, insertable = false)
    private String name;

    @Column(name = "parent_id", length = 8, updatable = false, insertable = false)
    private String parentId;

    @Column(name = "level", nullable = false, updatable = false, insertable = false)
    private Integer level;

    // 자기 참조 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    // Markets와의 관계
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Market> markets = new ArrayList<>();

    // Searches와의 관계
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Search> searches = new ArrayList<>();
}
