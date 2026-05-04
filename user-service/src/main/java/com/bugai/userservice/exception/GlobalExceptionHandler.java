package com.bugai.userservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

// @RestControllerAdvice tells Spring:
// "Watch ALL controllers in this project"
// "If any exception is thrown, come to this class first"
// "Don't let raw errors reach the client"
@RestControllerAdvice
public class GlobalExceptionHandler {

    // LoggerFactory is from SLF4J library (included in Spring Boot automatically)
    // log.error() prints full stacktrace in YOUR console
    // client never sees this — only you as developer see it
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    // ─────────────────────────────────────────────────────
    // HANDLER 1 — User Not Found (404)
    // ─────────────────────────────────────────────────────
    // Triggered when: userRepository.findById() returns empty
    // and Service throws → new UserNotFoundException("User not found: 123")
    // UserNotFoundException carries that message via super(message)
    // ex.getMessage() gives us back → "User not found: 123"
    // build() wraps it into ErrorResponse and returns 404
    // ─────────────────────────────────────────────────────
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return build1(HttpStatus.NOT_FOUND, ex.getMessage());
    }


    // ─────────────────────────────────────────────────────
    // HANDLER 2 — Validation Failed (400)
    // ─────────────────────────────────────────────────────
    // Triggered when: @Valid fails on RequestDTO in Controller
    // Example: firstName is blank, email is invalid
    //
    // MethodArgumentNotValidException is NOT your class
    // Spring throws it automatically when @Valid fails — just import it
    //
    // getBindingResult().getFieldErrors() → gives list of all failed fields
    // .stream().map() → converts each error to "fieldName: error message"
    // .collect(Collectors.joining(", ")) → joins all into one string
    //
    // Example output message:
    // "firstName: First name is required, email: Invalid email format"
    // ─────────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        // collect all field errors into one message
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return build1(HttpStatus.BAD_REQUEST, message);
    }


    // ─────────────────────────────────────────────────────
    // HANDLER 3 — Business Logic Error (400)
    // ─────────────────────────────────────────────────────
    // Triggered when: Service throws → new IllegalStateException("Email already exists")
    // Example: user tries to register with an email that already exists in DB
    //
    // This is not a "not found" error — it's a bad request from client side
    // So we return 400 BAD REQUEST
    // ─────────────────────────────────────────────────────
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return build1(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    // ─────────────────────────────────────────────────────
    // HANDLER 4 — Catch All (500)
    // ─────────────────────────────────────────────────────
    // Triggered when: any unexpected exception is thrown
    // that is NOT caught by handlers 1, 2, or 3 above
    //
    // Example: DB connection fails, NullPointerException, etc.
    // log.error() prints the REAL error in your console so YOU can debug
    // We don't send the real error message to client (security risk)
    // Client just gets "Something went wrong"
    // ─────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        // real error printed in YOUR console/logs — you see full stacktrace
        // client never sees this
        log.error("Unexpected error occurred: ", ex);
        return build1(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
    }


    // ─────────────────────────────────────────────────────
    // HELPER METHOD — build()
    // ─────────────────────────────────────────────────────
    // This is NOT an inbuilt method — we wrote it ourselves
    // Purpose: avoid repeating the same 5 lines in every handler
    //
    // Takes: status (404/400/500) and message ("User not found")
    // Does:  creates ErrorResponse object with message + status + timestamp
    // Returns: ResponseEntity wrapping that ErrorResponse
    //
    // status.value() converts:
    // HttpStatus.NOT_FOUND → 404
    // HttpStatus.BAD_REQUEST → 400
    // HttpStatus.INTERNAL_SERVER_ERROR → 500clau
    // ─────────────────────────────────────────────────────
    private ResponseEntity<ErrorResponse> build1(HttpStatus status, String message) {

        ErrorResponse response = ErrorResponse.builder()  // Lombok's builder() → returns a Builder object (NOT your method)
                .message(message)                         // Lombok → sets message on Builder object
                .status(status.value())                   // Lombok → sets status on Builder object
                .timestamp(LocalDateTime.now())           // Lombok → sets timestamp on Builder object
                .build();                                 // Lombok's .build() → lives on Builder object, creates final ErrorResponse
        // ↑ this is NOT your build() — different object, same name, unrelated

        return ResponseEntity.status(status).body(response);
        // your private build() method ends here
        // your build() lives on GlobalExceptionHandler
        // Lombok's .build() lives on ErrorResponse.Builder
        // they just share the same name — no recursion, no conflict
    }

}