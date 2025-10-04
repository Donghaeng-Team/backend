package com.bytogether.userservice.util.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class Oauth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure (HttpServletRequest request,
                                        HttpServletResponse response,
                                         AuthenticationException exception) throws IOException {

        log.error("KaKao Login Failure, Error: {}", exception.getMessage());


        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost/3000/login")
                .queryParam("error", "oauth2 failed")
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
