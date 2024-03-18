package com.pkg.littlewriter.service;

import com.pkg.littlewriter.persistence.UserRepository;
import com.pkg.littlewriter.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepository.findByUsername(username));
    }

    public CustomUserDetails loadUserById(Long id) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepository.findById(id));
    }
}
