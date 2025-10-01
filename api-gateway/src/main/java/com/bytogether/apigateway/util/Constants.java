package com.bytogether.apigateway.util;

public class Constants {
    public static final String[] PUBLIC_PATH = {
            "/api/v1/**/public/**",
            "/actuator/**",
            "/websocket/**",            // 웹소켓 핸드쉐이크
    };
}
