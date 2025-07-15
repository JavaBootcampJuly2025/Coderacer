package com.coderacer.repository;

import com.coderacer.model.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUsername(String username);
    Account findByEmail(String email);
    boolean existsByEmail(@Email String email);
    boolean existsByUsername(@NotBlank @Size(min = 4, max = 25) @Pattern(regexp = "^[a-zA-Z0-9_]+$") String username);
}
