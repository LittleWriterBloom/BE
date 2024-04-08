package com.pkg.littlewriter.config;

import com.pkg.littlewriter.security.*;
import com.pkg.littlewriter.service.KakaoOauth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.cors.CorsConfigurationSource;
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
    @Autowired
    private CorsConfigurationSource littleWriterConfigSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(littleWriterConfigSource));
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/", "/auth/**", "swagger-ui/**", "/v3/api-docs/**", "/books/board/**", "/character/board/**","/h2-console/**").permitAll()
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
        http.headers(headerConfigurer -> headerConfigurer.frameOptions(option -> option.disable()));
        http.exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint()));
        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
        return http.build();
    }
}
