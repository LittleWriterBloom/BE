package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.model.MemberEntity;
import com.pkg.littlewriter.model.SocialMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<MemberEntity, String> {
    MemberEntity findById(Long id);
    MemberEntity findByUsername(String username);
    Boolean existsByUsername(String username);
}
