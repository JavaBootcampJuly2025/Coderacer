package com.coderacer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "level_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Multiple LevelSessions can refer to the same Level
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    // Multiple LevelSessions can refer to the same Account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private double cpm;

    @Column(nullable = false)
    private double accuracy;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    public LevelSession(Level level, Account account, double cpm, double accuracy, LocalDateTime startTime, LocalDateTime endTime) {
        this.level = level;
        this.account = account;
        this.cpm = cpm;
        this.accuracy = accuracy;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
