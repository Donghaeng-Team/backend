package com.bytogether.chatservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * AOP 자동화를 위한 Aspect
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-21
 */

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublishAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * @RedisPublish 어노테이션이 있는 메서드 자동 처리
     * Service 메서드에 적용
     */
    @Around("@annotation(redisPublish)")
    public Object publishToRedis(ProceedingJoinPoint joinPoint, RedisPublish redisPublish) throws Throwable {
        // 1. 원본 메서드 실행
        Object result = joinPoint.proceed();

        // 2. 반환값이 null이면 발행 안 함
        if (result == null) {
            return result;
        }

        // 3. 채널명 결정
        String channel = redisPublish.channel();
        if (channel.isEmpty()) {
            // 채널명이 없으면 메서드 파라미터에서 추출
            channel = extractChannelFromParameters(joinPoint);
        }

        // 4. Redis 발행
        if (!channel.isEmpty()) {
            publishMessage(channel, result);
        }

        return result;
    }

    /**
     * 메서드 파라미터에서 채널명 추출
     *
     * 예: sendMessage(Long roomId, ...) -> "chat:room:123"
     */
    private String extractChannelFromParameters(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        // roomId 파라미터 찾기
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = param.getName();

            if ("roomId".equals(paramName) || "chatRoomId".equals(paramName)) {
                return "chat:room:" + args[i];
            }

            // @DestinationVariable 어노테이션이 있는 경우
            DestinationVariable destVar = param.getAnnotation(DestinationVariable.class);
            if (destVar != null) {
                String varName = destVar.value();
                if (varName.isEmpty()) {
                    varName = paramName;
                }
                if ("roomId".equals(varName)) {
                    return "chat:room:" + args[i];
                }
            }
        }

        log.warn("Cannot extract channel from method: {}", method.getName());
        return "";
    }

    /**
     * Redis로 메시지 발행
     */
    private void publishMessage(String channel, Object message) {
        try {
            log.debug("Publishing to Redis channel: {}", channel);
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            log.error("Failed to publish message to Redis channel: {}", channel, e);
            // 실패해도 로컬 WebSocket은 정상 동작
        }
    }
}