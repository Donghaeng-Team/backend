package com.bytogether.userservice.repository;

import com.bytogether.userservice.model.TokenAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenAuditLogRepository extends JpaRepository<TokenAuditLog, Long> {
}
