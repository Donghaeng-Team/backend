package com.bytogether.userservice.dto.oauth;

import com.bytogether.userservice.model.InitialProvider;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleUserInfo(final Map<String, Object> attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes cannot be null");
        }
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        Object userGoogleId = attributes.get("sub");
        if (userGoogleId == null) {
            throw new OAuth2AuthenticationException("Google user id not found");
        }
        return userGoogleId.toString();
    }

    public InitialProvider getProvider() {
        return InitialProvider.GOOGLE;
    }

    @Override
    public String getEmail() {
        Object userMail = attributes.get("email");
        if (userMail == null || ((String) userMail.toString().trim()).isEmpty()) {
            throw new OAuth2AuthenticationException("Google userEmail id not found");
        }
        return userMail.toString();
    }

    @Override
    public String getNickname() {
        Object userNickname =  attributes.get("name") ;
        if (userNickname == null) {
            throw new OAuth2AuthenticationException("Google user nickname was not found");
        }
        return userNickname.toString();
    }

    @Override
    public String getAvatar() {
        Object userAvatar = attributes.get("picture").toString();
        if (userAvatar == null) {
            throw new OAuth2AuthenticationException("Google user avatar was not found");
        }
        return userAvatar.toString();
    }
}