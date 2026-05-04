package com.bugai.userservice.service;

import com.bugai.userservice.dto.*;
import com.bugai.userservice.entity.*;
import com.bugai.userservice.exception.*;
import com.bugai.userservice.repository.*;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// @Service goes on the IMPL, not the interface
// Spring registers this class as the bean
// When controller asks for UserService (interface), Spring injects this impl automatically
@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    // injected via @RequiredArgsConstructor — no @Autowired needed


    // ─────────────────────────────────────────────────────
    // CREATE USER
    // ─────────────────────────────────────────────────────
    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {

        // STEP 1: check duplicate email
        // existsByEmail() → custom repo method → runs: SELECT EXISTS WHERE email = ?
        // throws IllegalStateException → caught by GlobalExceptionHandler → 400
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email already exists: " + dto.getEmail());
        }

        // STEP 2: map DTO → Entity
        // client sent UserRequestDTO, we build a User entity from it
        // we do NOT set id — @PrePersist on User entity generates UUID
        // we do NOT set createdAt/updatedAt — @CreationTimestamp/@UpdateTimestamp handle it
        User user = User.builder()
                .credentialsId(dto.getCredentialsId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .build();

        // STEP 3: save to DB → returns saved User with id + timestamps filled in
        // STEP 4: map Entity → ResponseDTO → return to controller
        return toResponse(userRepository.save(user));
    }


    // ─────────────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────────────
    @Override
    public UserResponseDTO getUserById(UUID id) {

        // findById() returns Optional<User>
        // orElseThrow() → if empty, throw UserNotFoundException
        // UserNotFoundException → caught by GlobalExceptionHandler → 404
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));

        // entity → DTO → return
        return toResponse(user);
    }


    // ─────────────────────────────────────────────────────
    // GET ALL USERS
    // ─────────────────────────────────────────────────────
    @Override
    public List<UserResponseDTO> getAllUsers() {

        // findAll() → List<User> (entities)
        // .stream() → process each element
        // .map(this::toResponse) → convert each User entity to UserResponseDTO
        // .toList() → collect back to List<UserResponseDTO>
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // ─────────────────────────────────────────────────────
    // UPDATE USER
    // ─────────────────────────────────────────────────────
    @Override
    public UserResponseDTO updateUser(UUID id, UserRequestDTO dto) {

        // STEP 1: fetch existing user — throws 404 if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));

        // STEP 2: update only allowed fields
        // firstName, lastName, phone → client can change these
        // email → NOT updated here (needs verification flow)
        // credentialsId → NEVER changes (links to Auth Service)
        // createdAt → NEVER changes
        // updatedAt → @UpdateTimestamp updates it automatically on save
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());

        // STEP 3: save → Hibernate detects existing id → runs UPDATE not INSERT
        return toResponse(userRepository.save(user));
    }


    // ─────────────────────────────────────────────────────
    // DELETE USER
    // ─────────────────────────────────────────────────────
    @Override
    public void deleteUser(UUID id) {

        // existsById() check first — deleteById() silently does nothing if id missing
        // we want to throw 404 so client knows the id was invalid
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }

        userRepository.deleteById(id);
        // void return — controller sends 204 No Content
    }


    // ─────────────────────────────────────────────────────
    // PRIVATE MAPPER — Entity → ResponseDTO
    // ─────────────────────────────────────────────────────
    // private — only this class uses it
    // single place where entity → DTO conversion happens
    // if you add a field later, you update it here only
    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .credentialsId(user.getCredentialsId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}