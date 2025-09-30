package com.bytogether.userservice.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value="refreshToken")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private Long userId;

    @Indexed  // 토큰으로도 검색 가능
    private String refreshToken;

    @TimeToLive
    private Long expiration=604800L; //7일
}