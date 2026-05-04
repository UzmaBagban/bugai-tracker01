package com.bugai.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable=false,unique = true)
    private UUID id; //SAME GENERATED IN AUTH SERVICE

    @NotBlank(message = "first name is required")
    @Column(nullable = false, length =50)
    private String firstName;

    @NotBlank(message ="Last Name is required")
    @Column(nullable = false, length =50)
    private String lastName;

    @Email (message = " Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "emai; is required")
    private String email;

    @Pattern(
            regexp = "^[0-9+]{10,15}$", message = "phone no must contain only digit and optional +")
    @Column(length =15)
    private String phone;

    @Column(nullable = false,updatable = false )
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        createdAt = LocalDateTime.now();

        if(id == null){
            throw new IllegalStateException("User id must come from AuthService");
        }
    }

    @PreUpdate
    public void onUpdate(){
        updatedAt = LocalDateTime.now();
    }



}
