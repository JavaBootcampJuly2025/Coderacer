package com.coderacer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Immutable
@Synchronize("accounts")
@Subselect(
        "SELECT " +
                "  a.id       AS id, " +
                "  a.username AS username, " +
                "  a.rating   AS matchmaking_rating " +
                "FROM accounts a " +
                "ORDER BY a.rating DESC"
)
@Check(constraints = "matchmaking_rating >= 0")
public class LeaderboardEntry {

    @Id
    @NotNull(message = "ID cannot be null")
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;


    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores")
    @Column(nullable = false, length = 25, updatable = false)
    private String username;

    @NotNull(message = "Matchmaking rating cannot be null")
    @Min(value = 0, message = "Matchmaking rating cannot be negative")
    @Column(nullable = false, updatable = false, columnDefinition = "INTEGER")
    private Integer matchmakingRating;
}