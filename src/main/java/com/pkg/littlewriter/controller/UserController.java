package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.dto.ResponseDTO;
import com.pkg.littlewriter.dto.UserDTO;
import com.pkg.littlewriter.domain.model.MemberEntity;
import com.pkg.littlewriter.security.TokenProvider;
import com.pkg.littlewriter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            if (userDTO == null || userDTO.getPassword() == null) {
                throw new RuntimeException("invalid password value.");
            }
            MemberEntity user = MemberEntity.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();
            MemberEntity registeredUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .username(registeredUser.getUsername())
                    .build();
            ResponseDTO<Object> responseDTO = ResponseDTO.builder()
                    .data(List.of(responseUserDTO))
                    .build();
            return ResponseEntity.ok()
                    .body(responseDTO);
        } catch (Exception e) {
            ResponseDTO<Object> responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest()
                    .body(responseDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        log.info("in controller");
        MemberEntity user = userService.getByCredentials(userDTO.getUsername(), userDTO.getPassword(), passwordEncoder);
        if (user != null) {
            String token = tokenProvider.create(user);
            UserDTO loginDTO = UserDTO.builder()
                    .username(user.getUsername())
                    .token(token)
                    .build();
            ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder()
                    .data(List.of(loginDTO))
                    .error("login success")
                    .build();
            return ResponseEntity.ok()
                    .body(responseDTO);
        }
        ResponseDTO<Object> responseDTO = ResponseDTO.builder()
                .error("login failed")
                .build();
        return ResponseEntity.badRequest()
                .body(responseDTO);
    }
}
