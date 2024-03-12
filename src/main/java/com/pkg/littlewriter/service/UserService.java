package com.pkg.littlewriter.service;

import com.pkg.littlewriter.model.UserEntity;
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

    public UserEntity create(UserEntity userEntity) {
        if (userEntity == null || userEntity.getUsername() == null) {
            throw new RuntimeException("Invalid arguments");
        }
        String username = userEntity.getUsername();
        if (userRepository.existsByUsername(username)) {
            log.warn("Username already exists {}", username);
            throw new RuntimeException("Username already exists");
        }
        return userRepository.save(userEntity);
    }

    public UserEntity getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
        UserEntity originalUser = userRepository.findByUsername(username);
        if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }
        return null;
    }

    public UserEntity getById(Long id) {
        return userRepository.findById(id);
    }

    public UserEntity getByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }
}
