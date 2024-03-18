package com.pkg.littlewriter.service;

import com.pkg.littlewriter.model.MemberEntity;
import com.pkg.littlewriter.model.SocialMemberEntity;
import com.pkg.littlewriter.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public MemberEntity create(MemberEntity memberEntity) {
        if (memberEntity == null || memberEntity.getUsername() == null) {
            throw new RuntimeException("Invalid arguments");
        }
        String username = memberEntity.getUsername();
        if (userRepository.existsByUsername(username)) {
            log.warn("Username already exists {}", username);
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(memberEntity);
    }

    public MemberEntity getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
        MemberEntity originalUser = userRepository.findByUsername(username);
        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }

    public MemberEntity getById(Long id) {
        return userRepository.findById(id);
    }

    public MemberEntity getByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }
}
