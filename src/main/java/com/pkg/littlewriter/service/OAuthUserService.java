package com.pkg.littlewriter.service;

import com.pkg.littlewriter.model.SocialUserEntity;
import com.pkg.littlewriter.model.UserEntity;
import com.pkg.littlewriter.persistence.SocialUserRepository;
import com.pkg.littlewriter.persistence.UserRepository;
import com.pkg.littlewriter.security.ApplicationOAuth2User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OAuthUserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;

    @Autowired
    private SocialUserRepository socialUserRepository;

    public OAuthUserService() {
        super();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);
//        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");

        try{
            log.info("OAuth2User attributes {}", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Long providedId = oAuth2User.getAttribute("id");
        String authProvider = userRequest.getClientRegistration().getClientName();
        String userName = authProvider + providedId.toString();
        log.info("username : {}", userName);
        if(socialUserRepository.existsByAuthProviderAndProvidedId(authProvider, providedId)) {
            log.info("social account exists");
            SocialUserEntity socialUserEntity = socialUserRepository.findByAuthProviderAndProvidedId(authProvider, providedId);
            return new ApplicationOAuth2User(socialUserEntity.getUser().getUsername(), oAuth2User.getAttributes());
        }
        UserEntity newUserEntity = UserEntity.builder()
                .username(userName)
                .authProvider(authProvider)
                .role(roleService.getByName("user").get(0))
                .build();
       userRepository.save(newUserEntity);
        SocialUserEntity socialUserEntity = SocialUserEntity.builder()
                .authProvider(authProvider)
                .providedId(providedId)
                .user(newUserEntity)
                .build();
        socialUserRepository.save(socialUserEntity);
        log.info("Successfully pulled user info username {} authProvider {}", userName, authProvider);
        return new ApplicationOAuth2User(newUserEntity.getUsername(), oAuth2User.getAttributes());
    }

    public SocialUserEntity findUserByAuthProviderAndProvidedId(String authProvider, Long providedId) {
        return socialUserRepository.findByAuthProviderAndProvidedId(authProvider, providedId);
    }

}
