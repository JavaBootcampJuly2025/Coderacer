package com.coderacer.service;

import com.coderacer.algo.RatingAlgorithm;
import com.coderacer.dto.*;
import com.coderacer.enums.Role;
import com.coderacer.exception.*;
import com.coderacer.model.Account;
import com.coderacer.model.Level;
import com.coderacer.repository.AccountRepository;
import com.coderacer.repository.LevelRepository;
import com.coderacer.model.EmailVerificationToken;
import com.coderacer.repository.EmailVerificationTokenRepository;
import com.coderacer.security.JWTUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final LevelRepository levelRepository; // for rating calc only
    private final RatingAlgorithm ratingAlgo; // for rating calc only
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final JWTUtil jwtUtil;

    @Transactional(readOnly = true)
    public AccountDTO getAccount(UUID id) {
        return accountRepository.findById(id)
                .map(AccountDTO::fromEntity)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .map(AccountDTO::fromEntity)
                .orElseThrow(() -> new AccountNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AccountDTO createAccount(AccountCreateDTO dto) {
        // Check for existing username/email
        if (accountRepository.existsByUsername(dto.username())) {
            throw new UsernameConflictException(dto.username());
        }
        if (accountRepository.existsByEmail(dto.email())) {
            throw new EmailConflictException(dto.email());
        }

        Account account = new Account();
        account.setUsername(dto.username());
        account.setEmail(dto.email());
        account.setPassword(dto.password()); // BCrypt encryption
        account.setRating(0);
        account.setVerified(false);
        account.setRole(Role.USER);

        Account saved = accountRepository.save(account);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, account, LocalDateTime.now().plusHours(24));
        emailVerificationTokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(account, token);

        return AccountDTO.fromEntity(saved);
    }

    @Transactional
    public AccountDTO updateAccount(UUID id, AccountUpdateDTO dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        // Update email if provided and different
        if (dto.email() != null && !dto.email().equals(account.getEmail())) {
            if (accountRepository.existsByEmail(dto.email())) {
                throw new EmailConflictException(dto.email());
            }
            account.setEmail(dto.email());
        }

        // Update rating if provided
        if (dto.rating() != null) {
            account.setRating(Math.max(dto.rating(), 0));
        }

        // Update verification status if provided
        if (dto.verified() != null) {
            account.setVerified(dto.verified());
        }

        Account saved = accountRepository.save(account);
        return AccountDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteAccount(UUID id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        accountRepository.deleteById(id);
    }

    @Transactional
    public void changePassword(UUID id, PasswordChangeDTO dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        // Verify current password
        if (!account.verifyPassword(dto.currentPassword())) {
            throw new PasswordVerificationException();
        }

        // Set new password (automatically hashed)
        account.setPassword(dto.newPassword());
        accountRepository.save(account);
    }

    @Transactional
    public ResponseEntity<String> verifyAccount(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }

        Account account = verificationToken.getAccount();
        account.setVerified(true);
        accountRepository.save(account);
        emailVerificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Email verified successfully. You can now log in.");
    }

    @Transactional
    public String attemptLogin(AccountLoginDTO accountLoginDTO) {
        Optional<Account> temp = accountRepository.findByUsername(accountLoginDTO.getUsername());
        if(temp.isEmpty()) {
            throw new AccountNotFoundException(accountLoginDTO.getUsername());
        }

        Account account = temp.get();

        if(!account.isVerified()) {
            throw new EmailNotVerifiedException("Account not verified");
        }

        if (!account.verifyPassword(accountLoginDTO.getPassword())) {
            throw new PasswordVerificationException();
        }

        return jwtUtil.generateToken(account.getUsername(), account.getRole().toString());
    }


    /**
     * Updates the account's rating based on new level session - a recently completed game by said player.
     *
     * @param dto we need accountId, levelId (for difficulty), cpm, and accuracy from this
     * @throws AccountNotFoundException if the account doesn't exist
     * @throws LevelNotFoundException if the level doesn't exist
     */
    @Transactional
    public void updateRating(LevelSessionCreateDto dto) {
        UUID accountId = dto.getAccountId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new LevelNotFoundException(dto.getLevelId()));

        double diffMultiplier = level.getDifficulty().getMultiplier();
        int performanceScore = (int) Math.round(dto.getCpm() * dto.getAccuracy() * diffMultiplier);

        // calculate how much to add/subtract
        int delta = ratingAlgo.calculateDelta(account.getRating(), performanceScore);

        // apply and persist
        account.setRating(account.getRating() + delta);
        accountRepository.save(account);
    }
}