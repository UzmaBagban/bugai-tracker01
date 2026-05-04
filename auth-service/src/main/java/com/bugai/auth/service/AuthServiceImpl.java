package com.bugai.auth.service;


import com.bugai.auth.dto.LoginRequest;
import com.bugai.auth.dto.RegisterRequest;
import com.bugai.auth.dto.AuthResponse;
import com.bugai.auth.entity.Credentials;
import com.bugai.auth.repository.CredentialsRepository;
import com.bugai.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of AuthService.
 * @RequiredArgsConstructor — Lombok generates constructor for all final fields (constructor injection).
 * @Slf4j — Lombok provides the 'log' logger backed by SLF4J.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Injected via constructor (not @Autowired — constructor injection is preferred)
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;  // BCrypt from SecurityConfig

    /**
     * Registers a new user.
     * @Transactional ensures the DB write is atomic — rolled back on exception.
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Guard: reject duplicate email early with a clear message
        if (credentialsRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Generate UUID here — this is the shared key sent to User Service
        UUID sharedId = UUID.randomUUID();

        // Build the Credentials entity using Lombok @Builder
        Credentials credentials = Credentials.builder()
                .id(sharedId)
                .email(request.getEmail())
                // Hash the plain-text password — NEVER store raw passwords
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .active(true)   // @Builder.Default also handles this; explicit for clarity
                .build();

        // Persist to bug_ai_auth.credentials table
        Credentials saved = credentialsRepository.save(credentials);
        log.info("User registered successfully with id: {}", saved.getId());

        // Return response DTO — client uses credentialsId to call POST /users
        return toResponse(saved);
    }

    /**
     * Logs in a user by verifying their email + password.
     * Read-only — no @Transactional write needed, but kept for session consistency.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Look up credentials by email; throw if not found
        Credentials credentials = credentialsRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if account is active (not suspended/soft-deleted)
        if (!credentials.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        // BCrypt.matches() compares plain-text input against stored hash
        if (!passwordEncoder.matches(request.getPassword(), credentials.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        log.info("Login successful for email: {}", request.getEmail());
        return toResponse(credentials);
    }

    /**
     * Private mapper — converts Credentials entity to AuthResponse DTO.
     * Keeps entity-to-DTO mapping in one place; never leaks passwordHash.
     */
    private AuthResponse toResponse(Credentials credentials) {
        return AuthResponse.builder()
                .credentialsId(credentials.getId())
                .email(credentials.getEmail())
                .role(credentials.getRole())
                .active(credentials.isActive())
                .build();
    }
}