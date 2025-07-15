package com.coderacer.service;


import com.coderacer.enums.Difficulty;
import com.coderacer.model.LeaderboardEntry;
import com.coderacer.repository.LeaderboardEntryRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class LeaderboardEntryServiceImpl implements LeaderboardEntryService {
    private static final double EASY_MULTIPLIER = 0.5;
    private static final double MEDIUM_MULTIPLIER = 1.0;
    private static final double HARD_MULTIPLIER = 1.5;


    private final LeaderboardEntryRepository repository;


    public LeaderboardEntryServiceImpl(LeaderboardEntryRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<LeaderboardEntry> getLeaderboardEntries() {
        List<LeaderboardEntry> entries = repository.findAll();


        for (LeaderboardEntry entry : entries) {
            double multiplier = getDifficultyMultiplier(entry.getDifficulty());
            double rating = entry.getAvgCpm() * (entry.getAccuracy() / 100) * multiplier;
            entry.setMatchmakingRating(rating);
        }


        return entries;
    }


    private double getDifficultyMultiplier(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return EASY_MULTIPLIER;
            case MEDIUM:
                return MEDIUM_MULTIPLIER;
            case HARD:
                return HARD_MULTIPLIER;
            default:
                throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }
    }
}
