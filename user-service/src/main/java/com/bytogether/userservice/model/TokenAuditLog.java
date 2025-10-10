package com.bytogether.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_audit_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenType tokenType;  // "ACCESS", "REFRESH"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Action action;     // "ISSUED", "REFRESHED", "REVOKED", "EXPIRED"

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
