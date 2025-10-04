package com.bytogether.userservice.dto.oauth;

import com.bytogether.userservice.model.InitialProvider;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import java.util.Map;

public class KakaoUerInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoUerInfo(final Map<String, Object> attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes cannot be null");
        }
        this.attributes = attributes;
    }

    //KaKao Id가져오기
    @Override
    public String getProviderId() {
        Object userKakaoId = attributes.get("id");
        if (userKakaoId == null) {
            throw new OAuth2AuthenticationException("Kakao user id not found");
        }
        return userKakaoId.toString();
    }

    @Override
    public InitialProvider getProvider() {
        return InitialProvider.KAKAO;
    }

    //이메일 가져오기 (사업자가 아닌경우 이메일 정보 불가하므로 kakaoId로서 식별가능한 내용 생성)
    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = getKakaoAccount();
        String email = (String) kakaoAccount.get("email");
        if(email == null || email.trim().isEmpty()) {
           return temporalEmailAccount();
        }
        return email;
    }

    //사용자 nickname가져오기
    @Override
    public String getNickname() {
        Map<String, Object> kakaoProfile = getKakaoProfile();
        Object userNickname =  kakaoProfile.get("nickname") ;
        if (userNickname == null) {
            throw new OAuth2AuthenticationException("Kakao user nickname was not found");
        }
        return userNickname.toString();
    }

    //사용자 이미지 가져오기
    @Override
    public String getAvatar() {
        Map<String, Object> profile = getKakaoProfile();
        Object profileImage = profile.get("profile_image_url");
        if (profileImage == null) {
            throw new OAuth2AuthenticationException("Kakao user avatar was not found");
        }
        return profileImage.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoAccount() {
        Object kakaoAccountObj = attributes.get("kakao_account");
        if(kakaoAccountObj == null) {
            throw new OAuth2AuthenticationException("Kakao account information is missing");
        }if(!(kakaoAccountObj instanceof Map)) {
            throw new OAuth2AuthenticationException("Invalid Kakao account format");
        }
        return (Map<String, Object>) kakaoAccountObj;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoProfile() {
        Object kakaoProfile = getKakaoAccount().get("profile");
        if(kakaoProfile == null) {
            throw new OAuth2AuthenticationException("Kakao account information is missing");
        }if(!(kakaoProfile instanceof Map)) {
            throw new OAuth2AuthenticationException("Invalid Kakao account format");
        }
        return (Map<String, Object>) kakaoProfile;
    }

    private String temporalEmailAccount(){
        Object kakaoId = ((Number) attributes.get("id"));
        if (kakaoId == null) {
            throw new OAuth2AuthenticationException("Kakao user id not found");
        }
        return "kakao_" +kakaoId + "@auth.local";
    }
}
