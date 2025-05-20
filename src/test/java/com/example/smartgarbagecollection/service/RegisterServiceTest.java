package com.example.smartgarbagecollection.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.example.smartgarbagecollection.dto.RegisterRequest;
import com.example.smartgarbagecollection.enums.Role;
import com.example.smartgarbagecollection.model.AuthUser;
import com.example.smartgarbagecollection.model.Company;
import com.example.smartgarbagecollection.model.User;
import com.example.smartgarbagecollection.repository.AuthUserRepository;
import com.example.smartgarbagecollection.repository.CompanyRepository;
import com.example.smartgarbagecollection.repository.UserRepository;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@Disabled
class RegisterServiceTest {

    @InjectMocks
    private RegisterService registerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserDetailsImpl currentUser;

    private RegisterRequest registerRequest;
    private Company company;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the RegisterRequest
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("John Doe");
        registerRequest.setEmail("johndoe@example.com");
        registerRequest.setPhone("10469304348839");
        registerRequest.setUsername("johndoe123");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.EDITOR);
        registerRequest.setCompanyId(UUID.randomUUID());

        // Mock a Company
        company = new Company();
        company.setId(registerRequest.getCompanyId());

        // Mock currentUser with ADMIN role by default
        when(currentUser.getRole()).thenReturn(Role.ADMIN);
        when(currentUser.getCompanyId()).thenReturn(registerRequest.getCompanyId());
    }

    @Test
    void shouldRegisterUserSuccessfullyAsSuperAdmin() {
        // Arrange
        when(companyRepository.findById(registerRequest.getCompanyId())).thenReturn(Optional.of(company));
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(new AuthUser());
        when(passwordService.hashPassword(registerRequest.getPassword())).thenReturn("hashedPassword");

        // Act
        registerService.registerUser(registerRequest, currentUser);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(authUserRepository, times(1)).save(any(AuthUser.class));
    }

    @Test
    void shouldThrowExceptionIfCompanyNotFound() {
        // Arrange
        when(companyRepository.findById(registerRequest.getCompanyId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                registerService.registerUser(registerRequest, currentUser)
        );
        assertEquals("Компания не найдена", exception.getMessage());
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenNotSuperAdminCreatingSuperAdmin() {
        // Arrange
        when(currentUser.getRole()).thenReturn(Role.EDITOR);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                registerService.registerUser(registerRequest, currentUser)
        );
        assertEquals("Только ADMIN может создавать других ADMIN", exception.getMessage());
    }

    @Test
    void shouldThrowAccessDeniedExceptionIfUserHasInsufficientPermissions() {
        // Arrange
        registerRequest.setRole(Role.VIEWER);
        when(currentUser.getRole()).thenReturn(Role.EDITOR);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                registerService.registerUser(registerRequest, currentUser)
        );
        assertEquals("Вам запрещено создавать пользователей с ролью: VIEWER", exception.getMessage());
    }

    @Test
    void shouldThrowAccessDeniedExceptionIfCompanyAdminTriesToRegisterUserForAnotherCompany() {
        // Arrange
        UUID anotherCompanyId = UUID.randomUUID();
        registerRequest.setCompanyId(anotherCompanyId);
        when(currentUser.getRole()).thenReturn(Role.EDITOR);
        when(currentUser.getCompanyId()).thenReturn(UUID.randomUUID());

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                registerService.registerUser(registerRequest, currentUser)
        );
        assertEquals("Вы не можете регистрировать пользователей в другой компании", exception.getMessage());
    }

    // Test: COMPANY_ADMIN can create DRIVER or AUDITOR
    @Test
    void shouldAllowCompanyAdminToCreateDriver() {
        // Arrange
        registerRequest.setRole(Role.VIEWER);
        when(currentUser.getRole()).thenReturn(Role.EDITOR);

        // Act
        registerService.registerUser(registerRequest, currentUser);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(authUserRepository, times(1)).save(any(AuthUser.class));
    }

    @Test
    void shouldAllowCompanyAdminToCreateViewer() {
        // Arrange
        registerRequest.setRole(Role.VIEWER);
        when(currentUser.getRole()).thenReturn(Role.EDITOR);

        // Act
        registerService.registerUser(registerRequest, currentUser);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(authUserRepository, times(1)).save(any(AuthUser.class));
    }

    // Test: ADMIN can create any role
    @Test
    void shouldAllowSuperAdminToCreateAnyRole() {
        // Arrange
        registerRequest.setRole(Role.VIEWER);
        when(currentUser.getRole()).thenReturn(Role.ADMIN);

        // Act
        registerService.registerUser(registerRequest, currentUser);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(authUserRepository, times(1)).save(any(AuthUser.class));
    }

    // Test: AccessDeniedException when user without proper role tries to create a ADMIN
    @Test
    void shouldThrowAccessDeniedWhenNonSuperAdminTriesToCreateSuperAdmin() {
        // Arrange
        registerRequest.setRole(Role.ADMIN);
        when(currentUser.getRole()).thenReturn(Role.EDITOR);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                registerService.registerUser(registerRequest, currentUser)
        );
        assertEquals("Только SUPER_ADMIN может создавать других SUPER_ADMIN", exception.getMessage());
    }

    // Test: COMPANY_ADMIN should not be able to create ADMIN
    @Test
    void shouldNotAllowCompanyAdminToCreateSuperAdmin() {
        // Arrange
        registerRequest.setRole(Role.ADMIN);
        when(currentUser.getRole()).thenReturn(Role.EDITOR);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                registerService.registerUser(registerRequest, currentUser)
        );
        assertEquals("Только SUPER_ADMIN может создавать других SUPER_ADMIN", exception.getMessage());
    }
}

