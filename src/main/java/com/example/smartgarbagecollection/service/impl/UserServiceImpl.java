package com.example.smartgarbagecollection.service.impl;

import com.example.smartgarbagecollection.audit.Audit;
import com.example.smartgarbagecollection.dto.UserDto;
import com.example.smartgarbagecollection.dto.UserRequest;
import com.example.smartgarbagecollection.enums.Role;
import com.example.smartgarbagecollection.model.User;
import com.example.smartgarbagecollection.repository.AuthUserRepository;
import com.example.smartgarbagecollection.repository.UserCompanyRepository;
import com.example.smartgarbagecollection.repository.UserRepository;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import com.example.smartgarbagecollection.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;
    private final UserCompanyRepository userCompanyRepository;

    @Override
    public List<UserDto> getAllUsers(UserDetailsImpl currentUser) {
        List<User> users = userRepository.findAll();

        if (!Role.ADMIN.equals(currentUser.getRole())) {

            UUID currentCompanyId = currentUser.getCompanyId();
            users = users.stream()
                    .filter(user -> getCompanyIdOf(user).equals(currentCompanyId))
                    .toList();
        }

        return users.stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    @Override
    public UserDto getUserById(UUID userId, UserDetailsImpl currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (!Role.ADMIN.equals(currentUser.getRole()) &&
                !currentUser.getCompanyId().equals(getCompanyIdOf(user))) {
            throw new AccessDeniedException("Вы не можете просматривать пользователей из другой компании");
        }

        return UserDto.fromEntity(user);
    }

    @Audit(action = "UPDATE_USER")
    @Transactional
    public UserDto updateUser(UUID userId, UserRequest request, UserDetailsImpl currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        checkAccessToModify(user, currentUser);

        Optional.ofNullable(request.getFullName()).ifPresent(user::setFullName);
        Optional.ofNullable(request.getPhone()).ifPresent(user::setPhone);
        Optional.ofNullable(request.getPosition()).ifPresent(user::setPosition);

        return UserDto.fromEntity(userRepository.save(user));
    }

    @Audit(action = "DELETE_USER")
    @Transactional
    public void deleteUser(UUID userId, UserDetailsImpl currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        checkAccessToDelete(user, currentUser);

        userCompanyRepository.deleteByUserId(user.getId());
        authUserRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
    }

    public void checkAccessToModify(User targetUser, UserDetailsImpl currentUser) {
        if (!currentUser.getCompanyId().equals(getCompanyIdOf(targetUser))) {
            throw new AccessDeniedException("Вы не можете редактировать пользователей из другой компании");
        }
    }

    public void checkAccessToDelete(User targetUser, UserDetailsImpl currentUser) {
        Role deleterRole = currentUser.getRole();
        Role targetRole = targetUser.getRole();

        boolean allowed = switch (deleterRole) {
            case ADMIN -> true;
            case EDITOR -> targetRole == Role.VIEWER;
            default -> false;
        };

        if (!allowed) {
            throw new AccessDeniedException("Недостаточно прав для удаления пользователя с ролью: " + targetRole);
        }

        checkAccessToModify(targetUser, currentUser);
    }

    public UUID getCompanyIdOf(User user) {
        return userCompanyRepository.findByUserId(user.getId())
                .map(userCompany -> userCompany.getCompany().getId())
                .orElseThrow(() -> new IllegalStateException("Пользователь не привязан к компании"));
    }
}
