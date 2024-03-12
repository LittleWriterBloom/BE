package com.pkg.littlewriter.dto;

import com.pkg.littlewriter.model.SocialUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SocialUserDTO {
    private Long providedId;
    private String authProvider;
    private String email;
    private String nickName;

    public SocialUserDTO (SocialUserEntity socialUserEntity) {
        this.providedId = socialUserEntity.getProvidedId();
        this.authProvider = socialUserEntity.getAuthProvider();
        this.email = socialUserEntity.getEmail();
        this.nickName = socialUserEntity.getNickName();
    }
}
