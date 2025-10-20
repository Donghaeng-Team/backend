package com.bytogether.userservice.dto.oauth;

import com.bytogether.userservice.model.InitialProvider;

public interface OAuth2UserInfo {
    String getProviderId();
    InitialProvider getProvider();
    String getEmail();
    String getNickname();
    String getAvatar();
}




