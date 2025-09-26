package com.bytogether.marketservice.entity;


import jakarta.persistence.*;
import lombok.*;

/**
 * 장바구니 엔티티 클래스 - Cart
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "market_id", nullable = false, updatable = false)
    private Long marketId;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", insertable = false, updatable = false)
    private Market market;
}