package com.bytogether.userservice.security;

import com.bytogether.userservice.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatShouldBeLongEnough}")
    private String jwtSecret;

    @Value("${jwt.access-expiration:3600000}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshTokenExpiration;

    //1. 검증용 키 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    //2. 토큰 검증
    public Claims getPayload(String token){
        try{
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            log.info("토큰 검증 성공");
            return claims;
        }catch (ExpiredJwtException error){
            log.warn("토큰 오류 감지: {}", token);
            throw error;
        }
    }
    //3. 토큰 종류별 생성
    public String getAccessToken(Long userId, Role role){
        return generateToken( userId, role, "access", accessTokenExpiration);
    }
    public String getRefreshToken(Long userId, Role role){
        return generateToken(userId, role,"refresh", refreshTokenExpiration);
    }

    private String generateToken(Long userId, Role role, String tokenCategory,  Long expireMs){
       Map<String, Object> claims = new HashMap<>();
       claims.put("userId", userId);
       claims.put("role", role);
       claims.put("category", tokenCategory);
        return Jwts.builder()
               .claims(claims)
               .issuedAt(new Date(System.currentTimeMillis()))
               .expiration(new Date(System.currentTimeMillis() + expireMs))
               .signWith(getSigningKey())
               .compact();
    }
}
