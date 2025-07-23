// com/coderacer/runner/config/runnerSecurityConfig.java (Corrected Example)
package com.coderacer.runner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order; // Import @Order
import org.springframework.http.HttpMethod; // Import HttpMethod if needed
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // For csrf disable
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class RunnerSecurityConfig {

    @Bean
    @Order(1) // This filter chain should have a lower order (higher precedence)
    // than your general SecurityConfig.filterChain (which implicitly has a higher order if not specified, or you can explicitly set it to @Order(100) or similar).
    public SecurityFilterChain runnerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Apply this filter chain ONLY to paths under /api/v1/code/
                .securityMatcher("/api/v1/code/**")
                .csrf(AbstractHttpConfigurer::disable) // Typically disable CSRF for stateless APIs
                .authorizeHttpRequests(auth -> auth
                        // Allow POST to /api/v1/code/execute without authentication
                        .requestMatchers(HttpMethod.POST, "/api/v1/code/execute").permitAll()
                        // Any other request under /api/v1/code/ (if you add more endpoints later)
                        // could require authentication, or be denied, based on your needs.
                        // For now, if only /execute is needed, you might not need more rules here.
                        // IMPORTANT: DO NOT use .anyRequest() here if SecurityConfig has it.
                        // If you need other paths under /api/v1/code/ to be authenticated,
                        // you would add specific .requestMatchers().authenticated() rules.
                        .anyRequest().denyAll() // Example: deny all other requests within /api/v1/code/**
                );
        return http.build();
    }
}