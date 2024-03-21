package com.pkg.littlewriter.service;

import com.pkg.littlewriter.domain.model.SocialMemberEntity;
import com.pkg.littlewriter.domain.model.MemberEntity;
import com.pkg.littlewriter.persistence.SocialUserRepository;
import com.pkg.littlewriter.persistence.UserRepository;
import com.pkg.littlewriter.security.CustomOAuth2User;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KakaoOauth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocialUserRepository socialUserRepository;

    public KakaoOauth2UserService() {
        super();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);
        Long providedId = oAuth2User.getAttribute("id");
        String authProvider = userRequest.getClientRegistration().getClientName();
        if (socialMemberExists(providedId, authProvider)) {
            log.info("social member exists");
            SocialMemberEntity existSocialMemberEntity = socialUserRepository.findByAuthProviderAndProvidedId(authProvider, providedId);
            return new CustomOAuth2User(existSocialMemberEntity.getMember(), oAuth2User.getAttributes());
        }
        assert providedId != null;
        MemberEntity newMemberEntity = createSocialMember(providedId, authProvider);
        log.info("Successfully pulled user info authProvider {}", authProvider);
        return new CustomOAuth2User(newMemberEntity, oAuth2User.getAttributes());
    }

    private boolean socialMemberExists(Long providedId, String authProvider) {
        return socialUserRepository.existsByAuthProviderAndProvidedId(authProvider, providedId);
    }

    @NotNull
    private MemberEntity createSocialMember(Long providedId, String authProvider) {
        String userName = authProvider + providedId.toString();
        MemberEntity newMemberEntity = MemberEntity.builder()
                .username(userName)
                .authProvider(authProvider)
                .build();
        userRepository.save(newMemberEntity);
        SocialMemberEntity socialMemberEntity = SocialMemberEntity.builder()
                .authProvider(authProvider)
                .providedId(providedId)
                .member(newMemberEntity)
                .build();
        socialUserRepository.save(socialMemberEntity);
        return newMemberEntity;
    }
}
