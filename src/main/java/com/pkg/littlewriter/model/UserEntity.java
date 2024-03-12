package com.pkg.littlewriter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    private String password;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
    private String authProvider;
    @OneToMany(mappedBy = "user")
    private List<SocialUserEntity> socialUserEntities;
}
