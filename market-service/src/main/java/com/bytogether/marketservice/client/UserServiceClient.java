package com.bytogether.marketservice.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Division Service와 통신하기 위한 Feign Client
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-31
 */

@FeignClient(name = "user-service")
public interface UserServiceClient {

}
