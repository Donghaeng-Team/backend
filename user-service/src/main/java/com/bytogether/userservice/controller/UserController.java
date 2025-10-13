package com.bytogether.userservice.controller;

import com.bytogether.userservice.dto.request.*;
import com.bytogether.userservice.dto.response.*;
import com.bytogether.userservice.service.UserService;
import com.bytogether.userservice.service.UserVerifyService;
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
     private final UserVerifyService userVerifyService;

     // 사용자 등록
    @PostMapping("/public/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("User Register Requested");
        userService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(null));

    }

    //로그인
    @PostMapping("/public/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse tokenIssued = userService.login(loginRequest) ;
        response.setHeader("Authorization", "Bearer "+ tokenIssued.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", tokenIssued.getRefreshToken(),7L);
        response.addCookie(newCookie);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    //로그아웃
    @DeleteMapping("/private/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("X-User-Id") Long userId, HttpServletResponse response) {
        userService.logout(userId, response);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    //refreshToken을 이용한 accessToken재발급 요청
    @PostMapping("/public/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(HttpServletRequest request, HttpServletResponse response) {
        LoginResponse tokenReissued = userService.refresh(request, response);
        response.setHeader("Authorization","Bearer " + tokenReissued.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", tokenReissued.getRefreshToken(),7L);
        response.addCookie(newCookie);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    //로그인된 사용자 정보 요청
    @GetMapping("/private/me")
    public ResponseEntity<ApiResponse<?>> userInfo(@RequestHeader("X-User-Id") Long userId, HttpServletResponse response) {
        log.info("현재 로그인된 사용자의 UserID:" + userId);
        Long currentUserId = userId;
        UserInfoResponse userInfoResponse = userService.findUserByUserId(currentUserId);
        log.info("로그인된 사용자 정보: " + userInfoResponse);
        return ResponseEntity.ok(ApiResponse.success(userInfoResponse));
    }

    //이메일로 사용자 가입여부 확인
    @GetMapping("/public/email")
    public ResponseEntity<ApiResponse<?>> checkRegisteredMail(@Valid @ModelAttribute EmailCheckRequest emailCheckRequest) {
        EmailCheckResponse response = userService.checkRegisteredMail(emailCheckRequest.getEmail());
        //Local 가입자인지 확인 추가 필요
        log.info("email 존재여부 : "+ response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //중복되는 닉네임인지 확인 필요
    @GetMapping("/public/nickname")
    public ResponseEntity<?> checkRegisteredNickname(@Valid @ModelAttribute NicknameCheckRequest nicknameCheckRequest) {
        NickNameCheckResponse response = userService.checkRegisteredNickname(nicknameCheckRequest.getNickname());
        //Local 가입자인지 확인 추가 필요
        log.info("nickname 존재여부 : "+ response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //사용자 ID를 이용한 정보조회
    @GetMapping("/private/userInfo")
    public ResponseEntity<ApiResponse<?>> userInfo(@Valid @ModelAttribute UserInfoRequest userInfoRequest) {
        Long targetUserId = userInfoRequest.getUserId();
        if(targetUserId == null){
            throw new IllegalStateException("사용자 정보 조회를 위한 Id는 필수입니다.");
        }
        UserInfoResponse targetUserInfoResponse = userService.findUserByUserId(targetUserId);
        log.info("로그인된 사용자 정보: " + targetUserInfoResponse);
        return ResponseEntity.ok(ApiResponse.success(targetUserInfoResponse));
    }

    //패스워드 변경 요청
    @PutMapping("/private/me/password")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestHeader("X-User-Id") Long userId,
                                                         @Valid @ModelAttribute ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(userId, changePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/private/me/nickname")
    public ResponseEntity<ApiResponse<?>> changeNickname(@RequestHeader("X-User-Id") Long userId,
                                                         @Valid @ModelAttribute ChangeNicknameRequest changeNicknameRequest) {
        ChangeNicknameResponse response = userService.changeNickname(userId, changeNicknameRequest );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //사용자 이메일 인증
    @PostMapping("/public/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest verifyRequest) {
        userVerifyService.verifyEmail(verifyRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    //사용자 이메일 재인증 요청
    @PostMapping("/public/reverify")
    public ResponseEntity<?> reverify(@RequestParam EmailRequestDto emailRequestDto) {
        userService.reverifyEmail(emailRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //패스워드 찾기 요청
    @PostMapping("/public/password/request-reset")
    public ResponseEntity<?> findPassword(@Valid @RequestBody EmailRequestDto emailRequestDto) {
        userService.findPassword(emailRequestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    //패스워드 찾기의 재설정
    @PostMapping("/public/password/confirm-reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        userVerifyService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

