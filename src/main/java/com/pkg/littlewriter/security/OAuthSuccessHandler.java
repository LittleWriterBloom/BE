package com.pkg.littlewriter.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final String LOCAL_REDIRECT_URI = "http://localhost:3000";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = tokenProvider.create(authentication);
        Optional<Cookie> oCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REDIRECT_URI_PARAM)).findFirst();
        Optional<String> redirectUri = oCookie.map(Cookie::getValue);
        response.sendRedirect(redirectUri.orElseGet(()->LOCAL_REDIRECT_URI) + "/sociallogin?token=" + token + "&&username=" + authentication.getName());
    }
}
