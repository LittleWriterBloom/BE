package com.pkg.littlewriter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@IdClass(SocialUserPK.class)
@Table(name = "social_user")
public class SocialUserEntity {
    @Id
    @Column(name = "auth_provider")
    private String authProvider;
    @Id
    @Column(name = "provided_id")
    private Long providedId;
    private String email;
    private String nickName;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
