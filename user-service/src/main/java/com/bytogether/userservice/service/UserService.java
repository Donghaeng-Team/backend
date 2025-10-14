package com.bytogether.userservice.service;

import com.bytogether.userservice.dto.request.*;
import com.bytogether.userservice.dto.response.*;
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
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MailService mailService;

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
                .verify(false)
                .build();

        log.info(newUser.toString());
        userRepository.save(newUser);
        //3. 이메일 인증 발송
        mailService.sendAuthEmailVerify(newUser.getEmail(), registerRequest.getNickname());
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest){
        //1. 사용자 조회 및 비번 일치확인
        User user = getOptionalUserByEmailAndProvider(
                loginRequest.getEmail(), InitialProvider.LOCAL)
                .orElseThrow(
                () -> new RuntimeException("사용자의 정보가 없거나 비밀번호가 일치하지 않습니다. 요청 이메일: " +loginRequest.getEmail())
        );
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadCredentialsException("사용자의 비밀번호가 일치하지 않습니다.");
        };
        Long userId = user.getId();
        Role role = user.getRole();

        //2. 이메일 인증 여부확인
//        if(!user.getVerify()){
//            throw new BadCredentialsException("이메일 인증이 완료되지 않았습니다");
//        }

        //3.Redis에서 저장된 refreshToken삭제 후 토큰 재발행
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
        //1. Toke 추출
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
        String roleString = claims.get("role", String.class);
        Role role = Role.valueOf(roleString);

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

    //사용자의 비밀번호 변경
    public void changePassword(Long userId, ChangePasswordRequest request){
        User user = getOptionalUserById(userId).orElseThrow(
                () -> new RuntimeException("사용자의 정보가 없습니다.")
        );

        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new BadCredentialsException("사용자의 비밀번호가 일치하지 않습니다");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("사용자의 비밀번호가 변경되었습니다");
    }

    //사용자의 닉네임 변경
    public ChangeNicknameResponse changeNickname(Long userId, ChangeNicknameRequest request){
        User user = getOptionalUserById(userId).orElseThrow(
                () -> new RuntimeException("사용자의 정보가 없습니다.")
        );
        if(existsByNickname(request.getNickname())){
            throw new IllegalStateException("사용할수 없는 닉네임 입니다");
        };

        String newNickname = request.getNickname();
        user.setNickname(newNickname);
        User savedUser = userRepository.save(user);
        log.info("사용자의 닉네임이 변경: "+ savedUser.getNickname());
        return ChangeNicknameResponse.builder()
                .nickname(savedUser.getNickname())
                .build();
    }

    //사용자의 비밀번호 찾기 기능
    public void findPassword(EmailRequestDto request){
        String userEmail = request.getEmail();
        User user = getOptionalUserByEmailAndProvider(userEmail, InitialProvider.LOCAL).orElseThrow(
                () -> new RuntimeException("사용자가 없습니다")
        );
        //사용자의 계정으로 이메일 발송
        mailService.sendPasswordEmailVerify(user.getEmail(), user.getNickname());
    }

    //사용자의 비밀번호 재설정
    public void reverifyEmail(EmailRequestDto request){
        String userEmail = request.getEmail();
        User user = getOptionalUserByEmailAndProvider(userEmail, InitialProvider.LOCAL).orElseThrow(
                () -> new RuntimeException("사용자가 없습니다")
        );
        //사용자의 계정으로 이메일 발송
        mailService.sendAuthEmailVerify(user.getEmail(), user.getNickname());
    }

    private Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Boolean existsByNickname(String nickname) {
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

   //다수의 사용자 정보조회
    public List<UserInternalResponse> findAllUSers(UsersInfoRequest request) {
        List<User> users = userRepository.findAllById(request.getUserIds());
        List<UserInternalResponse> responses = users.stream()
                .map(user -> new UserInternalResponse(
                        user.getId(),
                        user.getNickname(),
                        user.getAvatar()
                )).
                toList();
        return responses;
    }

    public Optional<User> getOptionalUserByEmailAndProvider(String email, InitialProvider provider) {
        Optional<User> result = userRepository.findByEmailAndProvider(email, provider);
        OptionalUserQueryResult( "email", email, result );
        return result;
    }

    public Optional<User> getOptionalUserById(Long userId) {
        Optional<User> result = userRepository.findById(userId);
        OptionalUserQueryResult( "userId", userId, result );
        return result;
    }

    public Optional<User> getOptionalUserByIdAndProvider(Long userId, InitialProvider provider) {
        Optional<User> result = userRepository.findByIdAndProvider(userId, provider);
        OptionalUserQueryResult( "userId", userId, result );
        return result;
    }

    private void OptionalUserQueryResult(String queryInfo, Object queryValue, Optional<User> optionalUser) {
        log.info("getOptionalUser with "+ queryInfo);
        log.info("조회 결과 존재 여부: {}", optionalUser.isPresent());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("조회된 사용자: ID={}, Email={}", user.getId(), user.getEmail());
        } else {
            log.error("사용자를 찾을 수 없음: {}", queryInfo);
        }
    }

}
