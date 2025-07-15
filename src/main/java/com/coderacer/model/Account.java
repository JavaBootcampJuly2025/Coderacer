package com.coderacer.model;

import com.coderacer.dto.AccountDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@Table(indexes = {
        @Index(name = "idx_username", columnList = "username")
        })
public class Account {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    private String hashedPassword;
    private int rating;
    private boolean verified;
}


