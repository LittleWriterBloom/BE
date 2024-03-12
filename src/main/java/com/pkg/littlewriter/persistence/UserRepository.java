package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findById(Long id);
    UserEntity findByUsername(String username);
    Boolean existsByUsername(String username);
    UserEntity findByUsernameAndPassword(String username, String password);
}
