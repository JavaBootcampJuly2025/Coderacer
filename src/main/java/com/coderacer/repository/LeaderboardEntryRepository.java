package com.coderacer.repository;

import com.coderacer.model.LeaderboardEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, UUID> {

    /**
     * Retrieves the top 10 entries by descending matchmakingRating.
     */
    List<LeaderboardEntry> findTop10ByOrderByMatchmakingRatingDesc();

    /**
     * Retrieves all entries sorted by matchmakingRating desc, paged (so you can request top N).
     */
    List<LeaderboardEntry> findAllByOrderByMatchmakingRatingDesc(Pageable pageable);

    /**
     * Look up a single entry by username.
     */
    Optional<LeaderboardEntry> findByUsername(String username);

    /**
     * Look up N entries of top players.
     */
    List<LeaderboardEntry> findTopNNative(@Param("limit") int limit);
}