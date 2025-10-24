package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.request.UsersInfoRequest;
import com.bytogether.marketservice.client.dto.response.UserInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * User Service와 통신하기 위한 Feign Client
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-31
 */

@FeignClient(name = "user-service",
        configuration = ServiceFeignConfig.class,
        url = "${openfeign.user-service.url}")
public interface UserServiceClient {

    @PostMapping("/internal/v1/user/usersinfo")
    List<UserInternalResponse> getUsersByIds(@RequestBody UsersInfoRequest usersInfoRequest);


}
