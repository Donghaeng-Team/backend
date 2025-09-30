package com.bytogether.userservice.service;

import com.bytogether.userservice.model.User;
import com.bytogether.userservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(Long userId, String refreshToken) {
        try {
            String key = "refreshToken:" + userId;
            redisTemplate.opsForValue().set(key, refreshToken);
        }catch (Exception e){
            log.error("토큰 저장 실패: {}", userId, e);
            throw new RuntimeException("토큰 저장 실패", e);
        }
    }

    public String getRefreshToken(Long userId) {
        String key = "refreshToken:" + userId;
        if(redisTemplate.opsForValue().get(key) == null){
            throw new RuntimeException("유효한 토큰이 없습니다.{}"+"User: "+userId);
        }
        return redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    public void deleteRefreshToken(Long userId) {
        try {
            Boolean tokenDeleted = redisTemplate.delete("refreshToken:" + userId);
            if(Boolean.FALSE.equals(tokenDeleted)){
                log.warn("삭제할 토큰이 없습니다.");
            }
        } catch(Exception e){
                log.error("userId: "+ userId +"의 토크을 삭제하는데 실패했습니다");
                throw new RuntimeException("userId: "+ userId +"의 토크을 삭제하는데 실패하였습니다.");
        }
    }

    //토큰이 null이 아니면서 refreshToken과 동일
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get("refreshToken:" + userId);
        log.info("토큰 정보:" + storedToken);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}
