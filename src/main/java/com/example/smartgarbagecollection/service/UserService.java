package com.example.smartgarbagecollection.service;

import com.example.smartgarbagecollection.dto.UserDto;
import com.example.smartgarbagecollection.dto.UserRequest;
import com.example.smartgarbagecollection.model.User;
import com.example.smartgarbagecollection.security.UserDetailsImpl;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDto> getAllUsers(UserDetailsImpl currentUser);
    UserDto getUserById(UUID userId, UserDetailsImpl currentUser);
    UserDto updateUser(UUID userId, UserRequest request, UserDetailsImpl currentUser);
    void deleteUser(UUID userId, UserDetailsImpl currentUser);

}
