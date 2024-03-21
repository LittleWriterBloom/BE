package com.pkg.littlewriter.security;

import com.pkg.littlewriter.domain.model.MemberEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final String userName;
    private final Long userId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(MemberEntity memberEntity, Map<String, Object> attributes) {
        this.userName = memberEntity.getUsername();
        this.attributes = attributes;
        this.userId = memberEntity.getId();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return this.userName;
    }

    public Long getUserId() {
        return this.userId;
    }
}
