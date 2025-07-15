package com.coderacer.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Subselect(
        "SELECT id, username, matchmaking_rating AS matchmakingRating " +
                "FROM account " +
                "ORDER BY matchmaking_rating DESC " +
                "LIMIT 20"
)
public class LeaderboardEntry {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    //private Account account;
    private String username;
    private int matchmakingRating;
}
