package com.bytogether.commservice.client;

import com.bytogether.commservice.client.dto.UserInternalResponse;
import com.bytogether.commservice.client.dto.UsersInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/internal/v1/user/userinfo")
    List<UserInternalResponse> getUserInfo(@RequestBody UsersInfoRequest userId);
}
