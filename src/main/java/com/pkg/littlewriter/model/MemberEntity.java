package com.pkg.littlewriter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    private String password;
    private String authority;
    private String authProvider;
    @OneToMany(mappedBy = "member")
    private List<SocialMemberEntity> socialMemberEntities;

    @PrePersist
    private void setDefaultPrivilege() {
        if(this.authority == null) {
            this.authority = "standard";
        }
    }
}
