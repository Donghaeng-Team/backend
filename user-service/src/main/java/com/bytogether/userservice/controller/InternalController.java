package com.bytogether.userservice.controller;

import com.bytogether.userservice.dto.request.UserInfoRequest;
import com.bytogether.userservice.dto.request.UsersInfoRequest;
import com.bytogether.userservice.dto.response.UserInfoResponse;
import com.bytogether.userservice.dto.response.UserInternalResponse;
import com.bytogether.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("internal/v1/user")
@RequiredArgsConstructor
public class InternalController {

    private final UserService userService;

    //내부 유저 정보
    @PostMapping("/usersinfo")
    public List<UserInternalResponse> getUsersInfo(@RequestBody UsersInfoRequest usersInfoRequest) {
       return userService.findAllUsers(usersInfoRequest);
    }

    //내부 유저 정보
    @PostMapping("/userinfo")
    public UserInfoResponse getUsersInfo(@RequestBody UserInfoRequest userInfoRequest) {
        return userService.findUserByUserId(userInfoRequest.getUserId());
    }
}
