package com.bytogether.userservice.service;

import com.bytogether.userservice.dto.request.EmailCheckRequest;
import com.bytogether.userservice.dto.request.LoginRequest;
import com.bytogether.userservice.dto.request.NicknameCheckRequest;
import com.bytogether.userservice.dto.request.RegisterRequest;
import com.bytogether.userservice.dto.response.EmailCheckResponse;
import com.bytogether.userservice.dto.response.LoginResponse;
import com.bytogether.userservice.dto.response.NickNameCheckResponse;
import com.bytogether.userservice.dto.response.UserInfoResponse;
import com.bytogether.userservice.model.*;
import com.bytogether.userservice.repository.RefreshTokenRepository;

import com.bytogether.userservice.repository.UserRepository;
import com.bytogether.userservice.security.JwtTokenProvider;
import com.bytogether.userservice.util.CookieUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenAuditLogService tokenAuditLogService;

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean existsByNickname(String nickname) {
       return userRepository.existsByNickname(nickname);
    }

    public UserInfoResponse findUserByUserId(Long userId) {
       User targetUser = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User with userId " + userId + " not found")
        );

       return UserInfoResponse.builder()
                .email(targetUser.getEmail())
                .nickName(targetUser.getNickname())
                .avatarUrl(targetUser.getAvatar())
                .build();
    }

    public UserInfoResponse findUsersByUserId(Long userId) {
        User targetUsers = userRepository.findUsersByIds(userId).orElseThrow(
                () -> new UsernameNotFoundException("User with userId " + userId + " not found")
        );

        return UserInfoResponse.builder()
                .email(targetUser.getEmail())
                .nickName(targetUser.getNickname())
                .avatarUrl(targetUser.getAvatar())
                .build();
    }


    //사용자 등록
    @Transactional
    public void register(RegisterRequest registerRequest){
        //1. 중복 체크 및 패스워드 점검
        if (existsByEmail(registerRequest.getEmail())) {
            throw new IllegalStateException("이미 가입된 이메일 계정입니다");
        }
        if (existsByNickname(registerRequest.getNickname())) {
            throw new IllegalStateException("이미 사용중인 닉네임입니다");
        }
        if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
            throw new BadCredentialsException("패스워드가 일치하지 않습니다");
        }

        //2. User 생성( Password Encryption ) 및 저장
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .nickname(registerRequest.getNickname())
                .password(registerRequest.getPassword())
                .provider(InitialProvider.LOCAL)
                .role(Role.USER)
//                .verify(false)
                .build();

        log.info(newUser.toString());
        userRepository.save(newUser);
//        //3. 이메일 인증 발송
//        mailService.sendAuthEmailVerify(newUser.getEmail(), registerRequest.getNickname());
    };

    @Transactional
    public LoginResponse login(LoginRequest loginRequest){
        //1. 사용자 조회
        User user = getOptionalUser(loginRequest.getEmail()).orElseThrow(
                () -> new RuntimeException("사용자의 정보가 없거나 비밀번호가 일치하지 않습니다. 요청 이메일: " +loginRequest.getEmail())
        );

        //2. 이메일 인증 여부확인
//        if(!user.getVerify()){
//            throw new BadCredentialsException("이메일 인증이 완료되지 않았습니다");
//        }

        //3. 토큰 발행
        Long userId = user.getId();
        Role role = user.getRole();

        //4.Redis에서 저장된 refreshToken삭제
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);

        LoginResponse newTokenResponse = authService.issueNewToken(userId, role);
        return newTokenResponse;
    };

    // Cookie의 refreshToken의 value와 기간 초기화, DB의 token삭제,
    public void logout(Long userId, HttpServletResponse response) {
        //1. redis에서 refreshToken삭제
        authService.deleteRefreshToken(userId);

        //2. Cookie에서 유효기간을 0으로 조정 후 교체
        Cookie cookie = CookieUtil.deleteCookie("refresh_token");
        response.addCookie(cookie);
        log.info("토큰의 유효기간이 0으로 재설정되었습니다.");
    }

    public LoginResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        //1. Token추출
        Optional<String> refreshTokenOptional = authService.getRefreshToken(request);
        if (refreshTokenOptional.isEmpty()) {
            throw new BadCredentialsException("Refresh Token을 발견하지 못했습니다.");
        }

        String refreshTokenValue = refreshTokenOptional.get();
        if(refreshTokenValue.isEmpty()){
            throw new BadCredentialsException("Refresh Token이 유효하지 않습니다");
        }

        //2. 토큰 검증
        Claims claims;
        try {
            claims = jwtTokenProvider.getPayload(refreshTokenValue);
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("Refresh Token의 유효기간이 만료되었습니다");
        }
        if (claims.getExpiration().before(new Date())) {
            throw new BadCredentialsException("Refresh Token이 만료되었습니다");
        }

        //3. 사용자 정보추출
        Long userId = claims.get("userId", Long.class);
        Role role = claims.get("role", Role.class);

        // 4.Redis에서 저장된 토큰과 비교
        RefreshToken storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BadCredentialsException("저장된 Refresh Token이 없습니다"));

        if (!storedToken.getRefreshToken().equals(refreshTokenValue)) {
            throw new BadCredentialsException("Refresh Token이 일치하지 않습니다");
        }
        //5.토큰 발행
        refreshTokenRepository.delete(storedToken); //새 토큰 저장을 위해 기존 토큰 삭제
        LoginResponse UpdatedTokenResponse = authService.updateToken(userId, role);
        return UpdatedTokenResponse;
    }

    //이미 사용된 Email체크
    public EmailCheckResponse checkRegisteredMail(String email) {
        boolean isRegisteredUser = userRepository.existsByEmail(email);
        return isRegisteredUser ?
                EmailCheckResponse.unavailable()
                : EmailCheckResponse.available();
    }

    //이미 사용된 NickName체크
    public NickNameCheckResponse checkRegisteredNickname(String nickname) {
        boolean isRegisteredUser = userRepository.existsByNickname(nickname);
        return isRegisteredUser ?
                NickNameCheckResponse.unavailable()
                : NickNameCheckResponse.available();
    }

    public Optional<User> getOptionalUser(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        log.info("getOptionalUser email: "+ email);
        log.info("조회 결과 존재 여부: {}", result.isPresent());
        if (result.isPresent()) {
            User user = result.get();
            log.info("조회된 사용자: ID={}, Email={}", user.getId(), user.getEmail());
        } else {
            log.error("사용자를 찾을 수 없음: {}", email);
        }
        return result;
    }

}
