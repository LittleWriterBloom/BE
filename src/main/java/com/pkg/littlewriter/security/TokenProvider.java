package com.pkg.littlewriter.security;

import com.pkg.littlewriter.domain.model.MemberEntity;
import com.pkg.littlewriter.service.KakaoOauth2UserService;
import com.pkg.littlewriter.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider implements InitializingBean {
    @Autowired
    private UserService userService;
    @Autowired
    private KakaoOauth2UserService oAuthUserService;
    @Value("${jwt.secret}")
    private String secret;
    private SecretKey key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String create(MemberEntity memberEntity) {
        Date expiryDate = Date.from(
                Instant.now()
                        .plus(1, ChronoUnit.DAYS));
        return Jwts.builder()
                .signWith(key)
                .subject(memberEntity.getId().toString())
                .issuer("littleWriter")
                .issuedAt(new Date())
                .expiration(expiryDate)
                .compact();
    }

    public String create(Authentication authentication) {
        CustomOAuth2User userPrincipal = (CustomOAuth2User) authentication.getPrincipal();
        Date expiryDate = Date.from(
                Instant.now()
                        .plus(1, ChronoUnit.DAYS));
        return Jwts.builder()
                .signWith(key)
                .subject(userPrincipal.getUserId().toString())
                .issuer("littleWriter")
                .issuedAt(new Date())
                .expiration(expiryDate)
                .compact();
    }

    public String validateAndGetUserId(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}


