package com.bytogether.commservice.client;

import com.bytogether.commservice.client.dto.UserDto;
import com.bytogether.commservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/v1/user/private/userInfo")
    ApiResponse<UserDto> getUserInfo(@RequestParam("userId") Long userId);
}
