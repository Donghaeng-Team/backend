package com.bytogether.marketservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 유저의 상품 조회 기록을 나타내는 엔티티 클래스입니다.
 * 각 조회 기록은 특정 유저가 특정 상품을 조회한 정보를 포함합니다.
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Entity
@Table(name = "views")
@Getter
@Setter
@NoArgsConstructor
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_id", nullable = false)
    private Long marketId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Market과의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", insertable = false, updatable = false)
    private Market market;
}