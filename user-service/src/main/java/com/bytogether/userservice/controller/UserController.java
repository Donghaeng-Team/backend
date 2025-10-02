package com.bytogether.userservice.controller;

import com.bytogether.userservice.dto.request.*;
import com.bytogether.userservice.dto.response.*;
import com.bytogether.userservice.service.UserService;
import com.bytogether.userservice.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(("api/v1/user"))
@RequiredArgsConstructor
public class UserController {
     private final UserService userService;
     private final CookieUtil cookieUtil;

    @PostMapping("/public/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("User Register Requested");
        userService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(null));

    }

    @PostMapping("/public/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse tokenIssued = userService.login(loginRequest) ;
        response.setHeader("authorization", tokenIssued.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", tokenIssued.getRefreshToken(),7L);
        response.addCookie(newCookie);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/private/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("X-User-Id") Long userId, HttpServletResponse response) {
        userService.logout(userId, response);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/public/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(HttpServletRequest request, HttpServletResponse response) {
        LoginResponse tokenReissued = userService.refresh(request, response);
        response.setHeader("authorization", tokenReissued.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", tokenReissued.getRefreshToken(),7L);
        response.addCookie(newCookie);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/private/me")
    public ResponseEntity<ApiResponse<?>> userInfo(@RequestHeader("X-User-Id") Long userId, HttpServletResponse response) {
        log.info("현재 로그인된 사용자의 UserID:" + userId);
        Long currentUserId = userId;
        UserInfoResponse userInfoResponse = userService.findUsersByUserId(currentUserId);
        log.info("로그인된 사용자 정보: " + userInfoResponse);
        return ResponseEntity.ok(ApiResponse.success(userInfoResponse));
    }

    @GetMapping("/public/email")
    public ResponseEntity<ApiResponse<?>> checkRegisteredMail(@Valid @ModelAttribute EmailCheckRequest emailCheckRequest) {
        EmailCheckResponse response = userService.checkRegisteredMail(emailCheckRequest.getEmail());
        log.info("email 존재여부 : "+ response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/public/nickname")
    public ResponseEntity<?> checkRegisteredNickname(@Valid @ModelAttribute NicknameCheckRequest nicknameCheckRequest) {
        NickNameCheckResponse response = userService.checkRegisteredNickname(nicknameCheckRequest.getNickname());
        log.info("nickname 존재여부 : "+ response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/private/userInfo")
    public ResponseEntity<ApiResponse<?>> userInfo(@Valid @ModelAttribute UserInfoRequest userInfoRequest) {
        Long targetUserID = userInfoRequest.getUserId();
        UserInfoResponse targetUserInfoResponse = userService.findUserByUserId(targetUserID);
        log.info("로그인된 사용자 정보: " + targetUserInfoResponse);
        return ResponseEntity.ok(ApiResponse.success(targetUserInfoResponse));
    }

//    @PostMapping("/public/verify")
//    public ResponseEntity<?> verify(@RequestBody VerifyRequest verifyRequest) {
//        userService.verify(verifyRequest);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

//    @PostMapping("/public/reverify")
//    public ResponseEntity<?> reverify(@RequestParam String email) {
//        authService.reverify(email);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

//    @PostMapping("/public/password/request-reset") //패스워드 찾기요청
//    public ResponseEntity<?> findPassword(@RequestBody FindPassWordRequest findPassWordRequest) {
//        userService.findPassword(findPassWordRequest);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    @PostMapping("/public/password/confirm-reset") //비밀번호 초기화 요청
//    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest changePasswordRequest){
//        userService.resetPassword(changePasswordRequest);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

}

