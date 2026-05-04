package com.bugai.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity  // takes full control of Spring Security configuration
public class SecurityConfig {

    /**
     * SecurityFilterChain bean — disables default form login and CSRF
     * (stateless REST API; CSRF not needed without session cookies).
     * Permits all requests to /auth/** without authentication.
     * Phase 2 will add JWT filter here.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — REST APIs with JWT are stateless; no session cookie
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // All /auth/** endpoints are public (register, login)
                        .requestMatchers("/auth/**").permitAll()
                        // Everything else requires authentication (Phase 2)
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * BCryptPasswordEncoder bean — strength 10 is the industry standard balance
     * between security and performance. Injected wherever password hashing is needed.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}