package com.coderacer.service;

import com.coderacer.model.LeaderboardEntry;

import java.util.List;

public interface LeaderboardEntryService {
    List<LeaderboardEntry> getLeaderboardEntries();
}
