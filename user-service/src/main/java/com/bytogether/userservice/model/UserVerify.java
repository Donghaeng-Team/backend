package com.bytogether.userservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "user_verify")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVerify {
    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 외래키를 pk 로 사용하기 위한 어노테이션
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    private VerifyType verifyType;
}