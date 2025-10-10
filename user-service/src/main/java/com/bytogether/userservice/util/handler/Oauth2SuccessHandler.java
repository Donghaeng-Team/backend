package com.bytogether.userservice.util.handler;

import com.bytogether.userservice.dto.response.LoginResponse;
import com.bytogether.userservice.model.Role;
import com.bytogether.userservice.model.User;
import com.bytogether.userservice.security.JwtTokenProvider;
import com.bytogether.userservice.service.AuthService;
import com.bytogether.userservice.service.Oauth2UserService;
import com.bytogether.userservice.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Oauth2UserService oauth2UserService;
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        //Kakao 회신에서 User의 정보 부분 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        //Authentication설정 , Provider확인
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        //개별 출력
        log.info("registrationId: {}", registrationId);
        log.info("oAuth2AuthenticationToken: {}", oAuth2AuthenticationToken);
        log.info("oAuth2User: {}", oAuth2User);

        //User Entity 저장후 정보 가져오기
        User user = oauth2UserService.processOAuth2User(oAuth2User, registrationId);
        log.info("User processed, Id:{}, Email:{}, Nickname:{}", user.getId(), user.getEmail(), user.getNickname());

        LoginResponse loginResponse = authService.issueNewToken(user.getId(), Role.USER);
        log.info("loginResponse: {}", loginResponse);
        response.setHeader("authorization", loginResponse.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", loginResponse.getRefreshToken(),7L);
        response.addCookie(newCookie);

        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/callback")
                .queryParam("access_token", loginResponse.getAccessToken())
                .build()
                .toUriString();

        log.info("Redirecting to: {}", redirectUrl);
        log.info("==========================================");

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}