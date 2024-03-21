package com.pkg.littlewriter.domain.model;

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
@IdClass(SocialMemberPK.class)
@Table(name = "social_member")
public class SocialMemberEntity {
    @Id
    @Column(name = "auth_provider")
    private String authProvider;
    @Id
    @Column(name = "provided_id")
    private Long providedId;
    private String email;
    private String nickName;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;
}
