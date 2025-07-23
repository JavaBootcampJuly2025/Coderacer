package com.coderacer.repository;

import com.coderacer.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    EmailVerificationToken findByToken(String token);
    void deleteByAccountId(UUID accountId);
    List<EmailVerificationToken> findByExpiryDateBefore(LocalDateTime time);
}
