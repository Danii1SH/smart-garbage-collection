package com.example.smartgarbagecollection.controller;

import com.example.smartgarbagecollection.dto.UserDto;
import com.example.smartgarbagecollection.dto.UserRequest;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import com.example.smartgarbagecollection.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает всех пользователей вашей компании"
    )
    public List<UserDto> getAllUsers(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return userService.getAllUsers(currentUser);
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по ID, если он принадлежит вашей компании"
    )
    public UserDto getUserById(@PathVariable UUID userId,
                               @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return userService.getUserById(userId, currentUser);
    }

    @PutMapping("/{userId}")
    @Operation(
            summary = "Обновить пользователя",
            description = "Позволяет изменить данные пользователя. Доступно ADMIN и EDITOR."
    )
    public UserDto updateUser(@PathVariable UUID userId,
                              @RequestBody UserRequest request,
                              @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return userService.updateUser(userId, request, currentUser);
    }

    @DeleteMapping("/{userId}")
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя. ADMIN может удалить любого, EDITOR — только VIEWER."
    )
    public void deleteUser(@PathVariable UUID userId,
                           @AuthenticationPrincipal UserDetailsImpl currentUser) {
        userService.deleteUser(userId, currentUser);
    }
}
