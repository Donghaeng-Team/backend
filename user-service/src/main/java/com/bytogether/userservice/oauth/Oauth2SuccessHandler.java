package com.bytogether.userservice.oauth;

import com.bytogether.userservice.dto.response.LoginResponse;
import com.bytogether.userservice.dto.response.TokenResponse;
import com.bytogether.userservice.model.User;
import com.bytogether.userservice.service.AuthService;
import com.bytogether.userservice.service.Oauth2UserService;
import com.bytogether.userservice.util.CookieUtil;
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
        log.info("oAuth2User Info: {}", oAuth2User.toString());

        //Authentication설정 , Provider확인
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        //User Entity 저장후 정보 가져오기
        User user = oauth2UserService.processOAuth2User(oAuth2User, registrationId);
        log.info("user info: {}",user.toString());

        TokenResponse tokenResponse = authService.issueNewToken(user.getId(), user.getRole());
        log.info("loginResponse: {}", tokenResponse);


        response.setHeader("authorization", tokenResponse.getAccessToken());
        Cookie newCookie = cookieUtil.createCookie("refresh_token", tokenResponse.getRefreshToken(),7L);
        response.addCookie(newCookie);

        String redirectUrl = UriComponentsBuilder.fromUriString("https://bytogether.net/auth/callback")
                .queryParam("provider", registrationId)
                .queryParam("access_token", tokenResponse.getAccessToken())
                .build()
                .toUriString();

        log.info("Redirecting to: {}", redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}