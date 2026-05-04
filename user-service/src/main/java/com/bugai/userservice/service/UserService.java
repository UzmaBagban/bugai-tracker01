package com.bugai.userservice.service;

import com.bugai.userservice.dto.UserRequestDTO;
import com.bugai.userservice.dto.UserResponseDTO;

import java.util.List;
import java.util.UUID;

// Interface defines the CONTRACT — what UserService CAN do
// Controller depends on this interface, not the implementation
// Makes unit testing easy — mock this interface, not the real class
public interface UserService {

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO getUserById(UUID id);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUser(UUID id, UserRequestDTO dto);

    void deleteUser(UUID id);
}