package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.model.SocialUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUserEntity, String> {
    Boolean existsByAuthProviderAndProvidedId(String authProvider, Long providedId);
    SocialUserEntity findByAuthProviderAndProvidedId(String authProvider, Long providedId);
}
