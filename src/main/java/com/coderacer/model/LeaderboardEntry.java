package com.coderacer.model;


import com.coderacer.enums.Difficulty;


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
                "ORDER BY matchmaking_rating DESC "
)
public class LeaderboardEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @ManyToOne // they want me to add this idk why
    @JoinColumn(name = "username_id")
    private Account username;

   private AccountMetrics avgCpm;
   private AccountMetrics accuracy;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    private double multiplier;


    private double matchmakingRating;
}
