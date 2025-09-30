package com.bytogether.apigateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter implements Filter {

    //Secret으로 Key설정
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatShouldBeLongEnough}")
    private String jwtSecret;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    //Filter적용
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpServletRequest sanitizedRequest = removeSecurityHeaders(httpRequest);

        String path = sanitizedRequest.getRequestURI();
        String method = sanitizedRequest.getMethod();
        System.out.println("JWT Filter - Path: " + path + ", Method: " + method);

        // 1. 공개 경로는 인증 없이 통과
        if (isPublicPath(path, method)) {
            System.out.println("JWT Filter: Public path, allowing request");
            chain.doFilter(sanitizedRequest, response);
            return;
        }

        // 2. JWT 토큰 검증
        String token = getTokenFromRequest(sanitizedRequest);
        if (token == null || !validateToken(token)) {
            System.out.println("JWT Filter: Invalid or missing token");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        //3. Claim추출
        Long userId = getUserIdFromToken(token);
        String userRole = getUserRoleFromToken(token);

        System.out.println("userId: " + userId);
        System.out.println("userRole: " + userRole);

        if(userId == null || userRole == null) {
            System.out.println("JWT Filter: Invalid or missing token");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"Invalid token claims\"}");
            return;
        }

        //4.관리자 경로 검증
        if(isAdminPath(path) && !hasAdminRole(userRole)){
            System.out.println("JWT Filter: Admin access denied");
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"error\":\"Admin access denied\"}");
            return;
        }

        //5. HttpRequest Header에 userId 추가
            CustomHttpServletRequestWrapper requestWrapper = new CustomHttpServletRequestWrapper(sanitizedRequest, userId);
            System.out.println("JWT Filter: Valid token, adding X-User-Id: " + userId);
            chain.doFilter(requestWrapper, response);
    }

    //관리자 role확인
    private boolean hasAdminRole(String role) {
        return "ADMIN".equals(role);
    }

    //Admin Path설정
    private boolean isAdminPath(String path) {
        return pathMatcher.match("/api/v1/admin/**", path);
    }

    private boolean isPublicPath(String path, String method) {
        if("OPTIONS".equals(method)) {
            return true;
        }
        if (pathMatcher.match("/api/v1/**/public/**", path)) {
            return true;
        }
        return false;
    }

    //Request의 헤더에서 Token추출
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    //토큰 검증
    private boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //사용자 ID추출
    private Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("userId", Long.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    //사용자 Role추출
    private String getUserRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private static class CustomHttpServletRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final Long userId;

        public CustomHttpServletRequestWrapper(HttpServletRequest request, Long userId) {
            super(request);
            this.userId = userId;
        }

        @Override
        public String getHeader(String name) {
            if ("X-User-Id".equals(name)) {
                return String.valueOf(userId);
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> headerNames = new HashSet<>();
            Enumeration<String> originalNames = super.getHeaderNames();
            while (originalNames.hasMoreElements()) {
                headerNames.add(originalNames.nextElement());
            }
            headerNames.add("X-User-Id");
            return Collections.enumeration(headerNames);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if ("X-User-Id".equals(name)) {
                return Collections.enumeration(Collections.singletonList(String.valueOf(userId)));
            }
            return super.getHeaders(name);
        }
    }

    private HttpServletRequest removeSecurityHeaders(HttpServletRequest httpRequest) {
        return new HttpServletRequestWrapper(httpRequest){
            @Override
            public String getHeader(String name) {
                if("X-User-Id".equals(name)) {
                    return null;
                }
                return super.getHeader(name);
            }
            @Override
            public Enumeration<String> getHeaders(String name) {
                if("X-User-Id".equals(name)) {
                    return Collections.emptyEnumeration();
                }
                return super.getHeaders(name);
            }
            @Override
            public Enumeration<String> getHeaderNames() {
                List<String> headerNames = Collections.list(super.getHeaderNames());
                headerNames.remove("X-User-Id");
                return Collections.enumeration(headerNames);
            }
        };
    }
}
