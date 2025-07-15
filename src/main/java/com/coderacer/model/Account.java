package com.coderacer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts",
        indexes = {
                @Index(name = "idx_account_username", columnList = "username", unique = true),
                @Index(name = "idx_account_email", columnList = "email", unique = true)
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_account_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uc_account_email", columnNames = {"email"})
        })
public class Account {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(length = 25, nullable = false)
    @NotBlank(message = "Username cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores")
    private String username;

    @NotNull
    @Column(length = 254, nullable = false) // 254 is RFC-compliant max email length
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotNull
    @Column(length = 97, nullable = false) // Length for BCrypt hash
    @NotBlank(message = "Password hash cannot be empty")
    private String hashedPassword;

    @Column(name = "rating", nullable = false)
    @Min(value = 0, message = "Rating cannot be negative")
    private int rating = 0;

    @NotNull
    @Column(nullable = false)
    private boolean verified;
}
