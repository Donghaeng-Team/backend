package com.bytogether.chatservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AOP 자동화를 위한 Aspect
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-15
 */

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublishAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // @SendTo 어노테이션이 있는 모든 메서드 자동 처리
    @Around("@annotation(sendTo)")
    public Object publishToRedis(ProceedingJoinPoint joinPoint, SendTo sendTo) throws Throwable {
        // 1. 원본 메서드 실행
        Object result = joinPoint.proceed();

        // 2. SendTo 대상 추출
        String[] destinations = sendTo.value();

        // 3. Redis로 자동 발행
        for (String destination : destinations) {
            publishMessage(destination, result);
        }

        return result;
    }

    // @SendToUser 처리
    @Around("@annotation(sendToUser)")
    public Object publishToUserRedis(ProceedingJoinPoint joinPoint, SendToUser sendToUser) throws Throwable {
        Object result = joinPoint.proceed();

        // 메서드 파라미터에서 userId 추출
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        String userId = extractUserId(parameters, args);
        if (userId != null) {
            String channel = "user:" + userId + ":" + sendToUser.value()[0];
            publishMessage(channel, result);
        }

        return result;
    }

    // 커스텀 어노테이션 처리
    @Around("@annotation(redisPublish)")
    public Object customRedisPublish(ProceedingJoinPoint joinPoint, RedisPublish redisPublish) throws Throwable {
        Object result = joinPoint.proceed();

        String channel = redisPublish.channel();
        if (channel.isEmpty()) {
            // 채널명 자동 생성 로직
            channel = generateChannelName(joinPoint);
        }

        publishMessage(channel, result);
        return result;
    }

    // MessageMapping 메서드도 자동 처리 (옵션)
    @After("@annotation(messageMapping) && args(.., message)")
    public void interceptMessageMapping(MessageMapping messageMapping, Object message) {
        String[] mappings = messageMapping.value();

        for (String mapping : mappings) {
            // 경로에서 변수 추출 (예: /chat.{roomId}.sendMessage)
            String channel = convertMappingToChannel(mapping);
            publishMessage(channel, message);
        }
    }

    private void publishMessage(String channel, Object message) {
        try {
            String redisChannel = normalizeChannel(channel);
            log.debug("Publishing to Redis channel: {}", redisChannel);

            // Redis Pub/Sub으로 발행
            redisTemplate.convertAndSend(redisChannel, message);
        } catch (Exception e) {
            log.error("Failed to publish message to Redis", e);
            // 실패해도 WebSocket은 정상 동작하도록
        }
    }

    private String normalizeChannel(String destination) {
        // /topic/rooms.123.messages -> redis:topic:rooms:123:messages
        return "redis:" + destination.replaceAll("[./]", ":");
    }

    private String extractUserId(Parameter[] parameters, Object[] args) {
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(DestinationVariable.class)
                    && "userId".equals(parameters[i].getName())) {
                return String.valueOf(args[i]);
            }
            // Principal에서 추출
            if (args[i] instanceof Principal) {
                return ((Principal) args[i]).getName();
            }
        }
        return null;
    }

    // 메서드 정보로부터 채널명 자동 생성
    private String generateChannelName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 클래스명과 메서드명으로 채널 생성
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();

        // ChatWebSocketController.sendMessage → redis:chat:sendMessage
        String channel = "redis:" + className.toLowerCase()
                .replace("controller", "")
                .replace("stomp", "")
                + ":" + methodName;

        // @DestinationVariable 값들도 포함
        Object[] args = joinPoint.getArgs();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof DestinationVariable) {
                    DestinationVariable destVar = (DestinationVariable) annotation;
                    String varName = destVar.value();
                    if (varName.isEmpty()) {
                        varName = method.getParameters()[i].getName();
                    }
                    channel += ":" + args[i];
                }
            }
        }

        return channel;
    }

    // MessageMapping 경로를 Redis 채널로 변환
    private String convertMappingToChannel(String mapping) {
        // /chat.{roomId}.sendMessage → redis:chat:roomId:sendMessage

        // 1. 앞의 / 제거
        String channel = mapping.startsWith("/") ? mapping.substring(1) : mapping;

        // 2. . 을 : 로 변경
        channel = channel.replace(".", ":");

        // 3. {변수} 패턴 처리
        // 실행 시점에 실제 값으로 치환되어야 하므로,
        // AOP 실행 컨텍스트에서 값을 가져와야 함
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(channel);

        while (matcher.find()) {
            String varName = matcher.group(1);
            // 실제 구현에서는 메서드 파라미터에서 해당 변수값을 찾아 치환
            // 여기서는 간단히 placeholder 유지
            channel = channel.replace("{" + varName + "}", varName);
        }

        // 4. redis: prefix 추가
        return "redis:" + channel;
    }

    // MessageMapping에서 실제 변수값 추출 (보조 메서드)
    private Map<String, Object> extractDestinationVariables(
            ProceedingJoinPoint joinPoint) {
        Map<String, Object> variables = new HashMap<>();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            DestinationVariable destVar = parameters[i].getAnnotation(DestinationVariable.class);
            if (destVar != null) {
                String varName = destVar.value();
                if (varName.isEmpty()) {
                    varName = parameters[i].getName();
                }
                variables.put(varName, args[i]);
            }
        }

        return variables;
    }
}