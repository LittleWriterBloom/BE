package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.domain.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<MemberEntity, String> {
    MemberEntity findById(Long id);
    MemberEntity findByUsername(String username);
    Boolean existsByUsername(String username);
}
