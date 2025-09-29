package com.bytogether.commservice.client;

import com.bytogether.commservice.client.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}