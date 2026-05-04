package com.bugai.auth.service;


import com.bugai.auth.dto.LoginRequest;
import com.bugai.auth.dto.RegisterRequest;
import com.bugai.auth.dto.AuthResponse;

/**
 * Interface for Auth Service business logic.
 * Interface + Impl pattern: allows easy mocking in tests
 * and clean separation of contract vs. implementation.
 */
public interface AuthService {

    // Registers a new user; returns UUID + details to client
    AuthResponse register(RegisterRequest request);

    // Validates credentials; returns auth details on success
    AuthResponse login(LoginRequest request);
}
