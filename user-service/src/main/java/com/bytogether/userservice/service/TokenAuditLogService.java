package com.bytogether.userservice.service;

import com.bytogether.userservice.model.Action;
import com.bytogether.userservice.model.TokenAuditLog;
import com.bytogether.userservice.model.TokenType;
import com.bytogether.userservice.repository.TokenAuditLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenAuditLogService {

    private final TokenAuditLogRepository tokenAuditLogRepository;

    public void saveTokenLog(Long userId, String token, TokenType tokenType, Action action) {
        TokenAuditLog log = TokenAuditLog.builder()
                .userId(userId)
                .token(token)
                .tokenType(tokenType)
                .action(action)
                .createdAt(LocalDateTime.now())
                .build();
        tokenAuditLogRepository.save(log);
    }
}
