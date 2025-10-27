package com.bytogether.userservice.service;

import com.bytogether.userservice.dto.oauth.GoogleUserInfo;
import com.bytogether.userservice.dto.oauth.KakaoUerInfo;
import com.bytogether.userservice.dto.oauth.OAuth2UserInfo;
import com.bytogether.userservice.model.Role;
import com.bytogether.userservice.model.User;
import com.bytogether.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Oauth2UserService{

    private final UserRepository userRepository;

    //OAuth2User 정보
    @Transactional
    public User processOAuth2User(OAuth2User oAuth2User, String registrationId) {

        // User정보 추출 ( Oauth Provider의 id와 수신된 user정보 )
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, oAuth2User);

        log.info("Processing OAuth2 user - Provider: {}, ProviderId: {}",
                oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());
        
        return userRepository.findByProviderIdAndDeletedAtIsNull(oAuth2UserInfo.getProviderId())
                .orElseGet(() -> createUser(oAuth2UserInfo));
    }

    //OAuth2User 정보 가져오기
    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, OAuth2User oAuth2User) {
        if ("kakao".equals(registrationId)) {
            return new KakaoUerInfo(oAuth2User.getAttributes());
        } else if ("google".equals(registrationId)) {
            return new GoogleUserInfo(oAuth2User.getAttributes());
        }
        throw new IllegalArgumentException("Unsupported provider: " + registrationId);
    }

    //Oauth2User 생성
    private User createUser(OAuth2UserInfo userInfo) {
        return userRepository.save(User.builder()
                .providerId(userInfo.getProviderId())
                .provider(userInfo.getProvider())
                .avatar(userInfo.getAvatar())
                .email(userInfo.getEmail())
                .role(Role.USER)
                .nickname(userInfo.getNickname()+"_"+userInfo.getProvider()+"_"+userInfo.getProviderId())
                .build());
    }
}