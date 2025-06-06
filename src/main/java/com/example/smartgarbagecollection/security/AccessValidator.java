package com.example.smartgarbagecollection.security;

import com.example.smartgarbagecollection.dto.RegisterRequest;
import com.example.smartgarbagecollection.enums.Role;
import com.example.smartgarbagecollection.model.User;
import com.example.smartgarbagecollection.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessValidator {

    private final CompanyService companyService;

    public boolean isAdmin(UserDetailsImpl user) {
        return Role.ADMIN.equals(user.getRole());
    }

    public boolean isEditor(UserDetailsImpl user) {
        return Role.EDITOR.equals(user.getRole());
    }

    public boolean isViewer(UserDetailsImpl user) {
        return Role.VIEWER.equals(user.getRole());
    }

    public List<User> filterByCompanyAccess(List<User> users, UserDetailsImpl currentUser) {
        if (isAdmin(currentUser)) return users;

        UUID currentCompanyId = currentUser.getCompanyId();

        return users.stream()
                .filter(user -> {
                    UUID userCompanyId = companyService.getCompanyIdByUserId(user.getId());
                    return currentCompanyId.equals(userCompanyId);
                })
                .toList();
    }

    public void checkSameCompany(UUID resourceCompanyId, UserDetailsImpl user) {
        if (!resourceCompanyId.equals(user.getCompanyId())) {
            throw new AccessDeniedException("Нет доступа к ресурсам другой компании");
        }
    }

    public void checkCanViewUser(User targetUser, UserDetailsImpl currentUser) {
        if (isAdmin(currentUser)) return;

        UUID targetCompanyId = companyService.getCompanyIdByUserId(targetUser.getId());
        checkSameCompany(targetCompanyId, currentUser);
    }

    public void checkCanModifyUser(User targetUser, UserDetailsImpl currentUser) {
        UUID targetCompanyId = companyService.getCompanyIdByUserId(targetUser.getId());
        checkSameCompany(targetCompanyId, currentUser);
    }

    public void checkCanDeleteUser(User targetUser, UserDetailsImpl currentUser) {
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

        checkCanModifyUser(targetUser, currentUser);
    }
    //добавить
    public static void checkCanRegister(UserDetailsImpl currentUser, RegisterRequest request, boolean isFirstUser) {
        if (isFirstUser) return;

        Role creatorRole = currentUser.getRole();
        Role targetRole = request.getRole();

        if (!Role.ADMIN.equals(creatorRole) && Role.ADMIN.equals(targetRole)) {
            throw new AccessDeniedException("Только ADMIN может создавать других ADMIN");
        }

        boolean isAllowed = switch (creatorRole) {
            case ADMIN -> true;
            case EDITOR -> targetRole == Role.VIEWER;
            default -> false;
        };

        if (!isAllowed) {
            throw new AccessDeniedException("Вам запрещено создавать пользователей с ролью: " + targetRole);
        }

        if (Role.EDITOR.equals(creatorRole)) {
            UUID editorCompanyId = currentUser.getCompanyId();
            UUID requestCompanyId = request.getCompanyId();

            if (requestCompanyId == null) {
                request.setCompanyId(editorCompanyId);
            } else if (!requestCompanyId.equals(editorCompanyId)) {
                throw new AccessDeniedException("Вы не можете указывать другую компанию");
            }
        }
    }
}