package com.pkg.littlewriter.config;

import com.pkg.littlewriter.security.OAuthAuthorizationRequestRepository;
import com.pkg.littlewriter.security.OAuthSuccessHandler;
import com.pkg.littlewriter.service.KakaoOauth2UserService;
import com.pkg.littlewriter.security.JwtAuthenticationFilter;
import com.pkg.littlewriter.security.OAuthFailureHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
public class WebSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private KakaoOauth2UserService oAuthUserService;
    @Autowired
    private OAuthAuthorizationRequestRepository oAuthAuthorizationRequestRepository;
    @Autowired
    private OAuthFailureHandler failureHandler;
    @Autowired
    private OAuthSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/", "/auth/**", "swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
        );
        http.oauth2Login(oauth2Login -> oauth2Login
                .authorizationEndpoint(configurer -> configurer
                        .baseUri("/auth/authorize")
                        .authorizationRequestRepository(oAuthAuthorizationRequestRepository
                        ))
                .redirectionEndpoint(configurer -> configurer.baseUri("/oauth2/callback/**"))
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oAuthUserService))
                .successHandler(successHandler)
                .failureHandler(failureHandler)
        );
        http.exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()));
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
        return http.build();
    }
}
