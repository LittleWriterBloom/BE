package com.pkg.littlewriter.dto;

import com.pkg.littlewriter.domain.model.SocialMemberEntity;
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

    public SocialUserDTO (SocialMemberEntity socialMemberEntity) {
        this.providedId = socialMemberEntity.getProvidedId();
        this.authProvider = socialMemberEntity.getAuthProvider();
        this.email = socialMemberEntity.getEmail();
        this.nickName = socialMemberEntity.getNickName();
    }
}
