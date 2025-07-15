package com.coderacer.service;

import com.coderacer.model.LeaderboardEntry;
import com.coderacer.repository.LeaderboardEntryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaderboardEntryServiceImpl implements LeaderboardEntryService{
    private final LeaderboardEntryRepository repository;

    public LeaderboardEntryServiceImpl(LeaderboardEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LeaderboardEntry> getLeaderboardEntries() {
        return repository.findAll();
    }
}
