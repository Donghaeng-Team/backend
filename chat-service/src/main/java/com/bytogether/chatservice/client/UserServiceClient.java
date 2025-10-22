package com.bytogether.chatservice.client;

import com.bytogether.chatservice.client.dto.UserInfoRequest;
import com.bytogether.chatservice.client.dto.UserInternalResponse;
import com.bytogether.chatservice.client.dto.UsersInfoRequest;
import jakarta.ws.rs.NotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;

/**
 * User Service와 통신하기 위한 Feign Client
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-22
 *
 */

@FeignClient(name = "user-service",url = "${openfeign.user-service.url}")
public interface UserServiceClient {

    @PostMapping("/usersinfo")
    List<UserInternalResponse> getUsersInfo(@RequestBody UsersInfoRequest usersInfoRequest);

    @PostMapping("/userinfo")
    UserInternalResponse getUserInfo(@RequestBody UserInfoRequest userInfoRequest);
}
