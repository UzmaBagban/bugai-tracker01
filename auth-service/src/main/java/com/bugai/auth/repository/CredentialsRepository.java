package com.bugai.auth.repository;


import com.bugai.auth.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Credentials.
 * UUID is the primary key type.
 */
@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {

    // Used during login to look up credentials by email
    Optional<Credentials> findByEmail(String email);

    // Used during registration to check for duplicate emails
    boolean existsByEmail(String email);
}
