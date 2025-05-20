package com.example.smartgarbagecollection.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRequest {

    @Schema(description = "Полное имя пользователя", example = "Иван Иванов", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fullName;

    @Schema(description = "Телефон", example = "+7 900 123-45-67", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phone;

    @Schema(description = "Позиция", example = "Водитель", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String position;
}
