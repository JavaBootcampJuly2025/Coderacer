package com.coderacer.dto;

import com.coderacer.model.LeaderboardEntry;

import java.util.UUID;

public record LeaderboardEntryDTO(
        UUID id,
        String username,
        Integer matchmakingRating
) {
    public static LeaderboardEntryDTO fromEntity(LeaderboardEntry entry) {
        return new LeaderboardEntryDTO(
                entry.getId(),
                entry.getUsername(),
                entry.getMatchmakingRating()
        );
    }
}