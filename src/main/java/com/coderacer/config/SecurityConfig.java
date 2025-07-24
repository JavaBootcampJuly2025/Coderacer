package com.coderacer.config;

import com.coderacer.security.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/accounts/login", "/api/accounts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/accounts/verify").permitAll()
                        .requestMatchers("/api/accounts/**").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/problems/{id}",
                                "/api/problems/search",
                                "/api/problems/random",
                                "/api/problems/random/difficulty/**").permitAll()
                        .requestMatchers("/api/problems/**").hasRole("ADMIN")
                        .requestMatchers("/api/metrics/**").authenticated()
                        .requestMatchers("/api/leaderboard/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/levels/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/levels").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/levels/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/levels/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/levels/**").authenticated()
                        .requestMatchers("/api/v1/level-sessions/**").authenticated()
                        .requestMatchers("/api/test/**").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
