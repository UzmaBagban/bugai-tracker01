package com.bugai.userservice.controller;

import com.bugai.userservice.dto.*;
import com.bugai.userservice.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

// @RestController = @Controller + @ResponseBody
// every method return value is automatically written to HTTP response body as JSON
// @RequestMapping("/users") = all endpoints in this class start with /users
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor  // Lombok generates constructor for all `final` fields
// Spring sees that constructor and injects UserService automatically
public class UserController {

    // final = required field = Lombok picks it up for constructor injection
    // we depend on the INTERFACE (UserService), not the implementation (UserServiceImpl)
    // Spring figures out which impl to inject — currently only one exists so no ambiguity
    private final UserService userService;


    // ─────────────────────────────────────────────────────
    // POST /users — Create user
    // ─────────────────────────────────────────────────────
    // @RequestBody → Spring reads JSON from request body, converts to UserRequestDTO
    // @Valid → activates validation annotations on UserRequestDTO (@NotBlank, @Email etc.)
    //          if validation fails → MethodArgumentNotValidException → GlobalExceptionHandler → 400
    // 201 CREATED is the correct status for resource creation (not 200 OK)
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    // ─────────────────────────────────────────────────────
    // GET /users/{id} — Get user by ID
    // ─────────────────────────────────────────────────────
    // @PathVariable → extracts {id} from the URL and binds it to UUID id parameter
    // Spring auto-converts String "abc-123..." → UUID
    // if user not found → service throws UserNotFoundException → GlobalExceptionHandler → 404
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    // ─────────────────────────────────────────────────────
    // GET /users — Get all users
    // ─────────────────────────────────────────────────────
    // no path variable — just /users
    // returns list — could be empty list [] if no users exist, never null
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    // ─────────────────────────────────────────────────────
    // PUT /users/{id} — Update user
    // ─────────────────────────────────────────────────────
    // @PathVariable → which user to update
    // @Valid @RequestBody → validated DTO with new values
    // service handles "user not found" → 404
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }


    // ─────────────────────────────────────────────────────
    // DELETE /users/{id} — Delete user
    // ─────────────────────────────────────────────────────
    // 204 NO CONTENT = success but nothing to return
    // industry standard for DELETE — don't return "User deleted successfully" string
    // that's not RESTful — status code communicates the result, body is empty
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
        // ResponseEntity.noContent() → 204
        // .build() here is Spring's ResponseEntity.build() — creates response with no body
        // NOT Lombok, NOT your build() — Spring's own method
    }
}