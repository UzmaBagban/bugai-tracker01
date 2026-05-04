package com.bugai.userservice.repository;

import com.bugai.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRepository  extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByFirstName(String firstName);

    boolean existsByEmail(String email);
}
