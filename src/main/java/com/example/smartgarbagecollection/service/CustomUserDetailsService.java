package com.example.smartgarbagecollection.service;

import com.example.smartgarbagecollection.model.AuthUser;
import com.example.smartgarbagecollection.repository.AuthUserRepository;
import com.example.smartgarbagecollection.repository.UserCompanyRepository;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;
    private final UserCompanyRepository userCompanyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        UUID userId = authUser.getUser().getId();
        UUID companyId = userCompanyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Компания не найдена для пользователя"))
                .getCompany().getId();

        return new UserDetailsImpl(authUser, companyId);
    }
}

