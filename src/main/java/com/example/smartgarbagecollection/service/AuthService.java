package com.example.smartgarbagecollection.service;

import com.example.smartgarbagecollection.dto.LoginRequest;
import com.example.smartgarbagecollection.model.AuthUser;
import com.example.smartgarbagecollection.repository.AuthUserRepository;
import com.example.smartgarbagecollection.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUserRepository authUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public String login(LoginRequest request) {
        Optional<AuthUser> userOptional = authUserRepository.findByUsername(request.getUsername());
        if (userOptional.isPresent()) {
            AuthUser user = userOptional.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                UUID userId = user.getUser().getId();
                String username = user.getUsername();

                return jwtTokenProvider.generateToken(userId, username);
            }
        }
        throw new RuntimeException("Неверный логин или пароль");
    }
}