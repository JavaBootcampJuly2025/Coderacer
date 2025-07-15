package com.coderacer.repository;

import com.coderacer.model.LeaderboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository <LeaderboardEntry, UUID>{
}
