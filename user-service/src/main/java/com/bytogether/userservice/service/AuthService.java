package com.bytogether.userservice.service;

import com.bytogether.userservice.dto.response.LoginResponse;
import com.bytogether.userservice.model.*;
import com.bytogether.userservice.repository.RefreshTokenRepository;
import com.bytogether.userservice.repository.TokenAuditLogRepository;
import com.bytogether.userservice.repository.UserRepository;
import com.bytogether.userservice.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenAuditLogRepository tokenAuditLogRepository;
    private final TokenAuditLogService tokenAuditLogService;
    private final UserRepository userRepository;

    @Transactional
    public LoginResponse issueNewToken(Long userId, Role role) {
        String accessToken = jwtTokenProvider.getAccessToken(userId, role);
        String refreshToken = jwtTokenProvider.getRefreshToken(userId, role);

        // Redis 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // TokenAuditLog에 저장
        tokenAuditLogService.saveTokenLog(userId, accessToken, TokenType.ACCESS, Action.ISSUED);
        tokenAuditLogService.saveTokenLog(userId, refreshToken, TokenType.REFRESH, Action.ISSUED);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse updateToken(Long userId, Role role) {
        String accessToken = jwtTokenProvider.getAccessToken(userId, role);
        String refreshToken = jwtTokenProvider.getRefreshToken(userId, role);

        // Redis 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // TokenAuditLog에 저장
        tokenAuditLogService.saveTokenLog(userId, accessToken, TokenType.ACCESS, Action.REFRESHED);
        tokenAuditLogService.saveTokenLog(userId, refreshToken, TokenType.REFRESH, Action.REFRESHED);

        return new LoginResponse(accessToken, refreshToken);
    }

    //refreshToken 추출
    public Optional<String> getRefreshToken(HttpServletRequest request){
        if(request.getCookies() == null){
            return Optional.empty();
        }
        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals("refresh_token")){
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    public void deleteRefreshToken(Long userId) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(userId);
        log.info("토큰 조회 결과: {}", refreshToken.isPresent());

        refreshToken.ifPresent(refreshTokenRepository::delete);
        log.info("deleteToken 완료");
        tokenAuditLogService.saveTokenLog(userId, refreshToken.toString(), TokenType.REFRESH, Action.REVOKED);


    }

}
