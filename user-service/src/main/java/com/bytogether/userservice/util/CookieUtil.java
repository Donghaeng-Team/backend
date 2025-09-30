package com.bytogether.userservice.util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtil {
    public Cookie createCookie(String name, String value, long days){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge((int) Duration.ofDays(days).getSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false);

        return cookie;
    }

    public static Cookie deleteCookie(String name){
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

}
