package com.bugai.userservice.service;

import com.bugai.userservice.dto.*;


import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(UUID id);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    void deactivateUser(UUID id);
    void deleteUser(UUID id);
}