package com.example.smartgarbagecollection.dto;

import com.example.smartgarbagecollection.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Полное имя обязательно")
    @Size(min = 2, max = 100, message = "Полное имя должно быть от 2 до 100 символов")
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Телефон должен содержать от 10 до 15 цифр и может начинаться с '+'"
    )
    private String phone;

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 4, max = 50, message = "Имя пользователя должно быть от 4 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 100, message = "Пароль должен быть от 8 до 100 символов")
    private String password;


    private UUID companyId;

    @NotNull(message = "Роль обязательна")
    private Role role;

    @NotNull(message = "Позиция обязательна")
    private String position;

}
