package com.example.smartgarbagecollection.controller;

import com.example.smartgarbagecollection.dto.RegisterRequest;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import com.example.smartgarbagecollection.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Эндпоинты для аутентификации пользователей и регистрации")
public class RegisterController {

    private final RegisterService authService;

    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    @Operation(
            summary = "Регистрация нового пользователя (админом)",
            description = "Регистрация пользователей доступна только для ADMIN и EDITOR. " +
                    "EDITOR может регистрировать пользователей только внутри своей компании."
    )
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        authService.registerUser(request, currentUser);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }
}
