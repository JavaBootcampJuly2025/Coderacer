package com.coderacer.controller;

import com.coderacer.model.LeaderboardEntry;
import com.coderacer.service.LeaderboardEntryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")

public class LeaderboardEntryController {
    private final LeaderboardEntryService service;

    public LeaderboardEntryController(LeaderboardEntryService service) {
        this.service = service;
    }

    @GetMapping
    public List<LeaderboardEntry> getLeaderboard() {
        return service.getLeaderboardEntries();
    }
}
