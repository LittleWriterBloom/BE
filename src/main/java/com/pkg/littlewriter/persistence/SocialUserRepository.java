package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.model.SocialMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialMemberEntity, String> {
    Boolean existsByAuthProviderAndProvidedId(String authProvider, Long providedId);
    SocialMemberEntity findByAuthProviderAndProvidedId(String authProvider, Long providedId);
}
