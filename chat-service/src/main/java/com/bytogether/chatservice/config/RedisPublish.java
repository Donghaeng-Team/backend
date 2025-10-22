package com.bytogether.chatservice.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Redis Pub/Sub을 자동화할 커스텀 어노테이션
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-15
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisPublish {
    String channel() default "";
}