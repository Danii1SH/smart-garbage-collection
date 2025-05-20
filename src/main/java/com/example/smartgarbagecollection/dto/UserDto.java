package com.example.smartgarbagecollection.dto;

import com.example.smartgarbagecollection.enums.Role;
import com.example.smartgarbagecollection.model.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private String position;
    private Role role;

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setPosition(user.getPosition());
        dto.setRole(user.getRole());
        return dto;
    }
}
