package com.example.smartgarbagecollection.service;

import com.example.smartgarbagecollection.audit.Audit;
import com.example.smartgarbagecollection.dto.RegisterRequest;
import com.example.smartgarbagecollection.enums.Role;
import com.example.smartgarbagecollection.model.AuthUser;
import com.example.smartgarbagecollection.model.Company;
import com.example.smartgarbagecollection.model.User;
import com.example.smartgarbagecollection.model.UserCompany;
import com.example.smartgarbagecollection.repository.AuthUserRepository;
import com.example.smartgarbagecollection.repository.CompanyRepository;
import com.example.smartgarbagecollection.repository.UserCompanyRepository;
import com.example.smartgarbagecollection.repository.UserRepository;
import com.example.smartgarbagecollection.security.AccessValidator;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;
    private final PasswordService passwordService;
    private final CompanyRepository companyRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final AccessValidator accessValidator;

    @Audit(action = "CREATE_USER")
    @Transactional
    public void registerUser(RegisterRequest request, UserDetailsImpl currentUser) {
        boolean isFirstUser = authUserRepository.count() == 0;

        accessValidator.checkCanRegister(currentUser, request, isFirstUser);

        Company company = getCompanyById(request.getCompanyId());

        User user = createUser(request);
        createAuthUser(request, user);
        createUserCompany(user, company);
    }

    private Company getCompanyById(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Компания с ID " + companyId + " не найдена"));
    }

    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setPosition(request.getPosition());
        return userRepository.save(user);
    }

    private AuthUser createAuthUser(RegisterRequest request, User user) {
        AuthUser authUser = new AuthUser();
        authUser.setUser(user);
        authUser.setUsername(request.getUsername());
        authUser.setPasswordHash(passwordService.hashPassword(request.getPassword()));
        return authUserRepository.save(authUser);
    }

    private UserCompany createUserCompany(User user, Company company) {
        UserCompany userCompany = new UserCompany();
        userCompany.setUser(user);
        userCompany.setCompany(company);
        return userCompanyRepository.save(userCompany);
    }
}



