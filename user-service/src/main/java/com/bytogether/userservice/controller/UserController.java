package com.bytogether.userservice.controller;

import com.bytogether.userservice.dto.request.LoginRequest;
import com.bytogether.userservice.dto.request.RegisterRequest;
import com.bytogether.userservice.dto.response.ApiResponse;
import com.bytogether.userservice.dto.response.LoginResponse;
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
@RequiredArgsConstructor
public class UserController {
     private final UserService userService;
     private final UserService authService;
     private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success(null));

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse tokenIssued = userService.login(loginRequest) ;
        response.setHeader("authorization", tokenIssued.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", tokenIssued.getRefreshToken(),7L);
        response.addCookie(newCookie);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("X-User-Id") Long userId, HttpServletResponse response) {
        userService.logout(userId, response);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(HttpServletRequest request, HttpServletResponse response) {
        LoginResponse tokenReissued = userService.refresh(request, response);
        response.setHeader("authorization", tokenReissued.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", tokenReissued.getRefreshToken(),7L);
        response.addCookie(newCookie);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

//    @GetMapping("/email")
//    public ResponseEntity<?> checkRegisteredMail(@Valid @ModelAttribute EmailCheckRequest emailCheckRequest) {
//        return ResponseEntity.status(HttpStatus.OK).body(authService.checkRegisteredEmail(emailCheckRequest));
//    }

//    @PutMapping("/nickname")
//    public ResponseEntity<?> checkRegisteredNickname(HttpServletRequest request, HttpServletResponse response) {
//        userService.refresh(request, response);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

//    @PostMapping("/verify")
//    public ResponseEntity<?> verify(@RequestBody VerifyRequest verifyRequest) {
//        userService.verify(verifyRequest);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

//    @PostMapping("/reverify")
//    public ResponseEntity<?> reverify(@RequestParam String email) {
//        authService.reverify(email);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

//    @PostMapping("/password/") //패스워드 찾기요청
//    public ResponseEntity<?> findPassword(@RequestBody FindPassWordRequest findPassWordRequest) {
//        userService.findPassword(findPassWordRequest);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
//
//    @PostMapping("password/reset") //비밀번호 초기화 요청
//    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequest changePasswordRequest){
//        userService.resetPassword(changePasswordRequest);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

}

