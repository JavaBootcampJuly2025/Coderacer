package com.coderacer.service;

import com.coderacer.model.EmailVerificationToken;
import com.coderacer.repository.AccountRepository;
import com.coderacer.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void cleanupExpiredTokens() {
        List<EmailVerificationToken> expiredTokens = emailVerificationTokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        for (EmailVerificationToken token : expiredTokens) {
            if (token.getAccount() != null) {
                accountRepository.delete(token.getAccount());
            }

            emailVerificationTokenRepository.delete(token);
        }
    }
}
