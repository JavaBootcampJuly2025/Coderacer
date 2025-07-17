package com.coderacer.model;

import com.coderacer.enums.Difficulty;
import com.coderacer.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    // BCrypt consts
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final int BCRYPT_HASH_LENGTH = 60;
    private static final String BCRYPT_REGEX = "^\\$2[ayb]\\$.{56}$";

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

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @NotBlank(message = "Password hash cannot be empty")
    @NotNull
    @Column(length = BCRYPT_HASH_LENGTH, nullable = false)
    @NotBlank(message = "Password hash cannot be empty")
    @Size(min = BCRYPT_HASH_LENGTH, max = BCRYPT_HASH_LENGTH, message = "Password hash must be valid BCrypt format")
    @Pattern(regexp = BCRYPT_REGEX, message = "Invalid password hash format")
    private String hashedPassword;

    @Column(name = "rating", nullable = false)
    @Min(value = 0, message = "Rating cannot be negative")
    private int rating = 0;

    @NotNull
    @Column(nullable = false)
    private boolean verified;

    public void setPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        this.hashedPassword = PASSWORD_ENCODER.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, this.hashedPassword);
    }
}
