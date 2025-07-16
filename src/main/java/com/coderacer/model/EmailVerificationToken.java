package com.coderacer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String token;

    @ManyToOne
    private Account account;

    private LocalDateTime expiryDate;

    public EmailVerificationToken(String token, Account account, LocalDateTime localDateTime) {
        this.token = token;
        this.account = account;
        this.expiryDate = localDateTime;
    }
}
