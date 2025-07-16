package com.coderacer.controller;

import com.coderacer.dto.LeaderboardEntryDTO;
import com.coderacer.service.LeaderboardEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardEntryController {

    private final LeaderboardEntryService leaderboardService;

    @GetMapping("/top")
    public ResponseEntity<List<LeaderboardEntryDTO>> getTop10() {
        return ResponseEntity.ok(leaderboardService.getTop10());
    }

    @GetMapping
    public ResponseEntity<Page<LeaderboardEntryDTO>> getLeaderboard(Pageable pageable) {
        return ResponseEntity.ok(leaderboardService.getLeaderboard(pageable));
    }

    @GetMapping("/{username}")
    public ResponseEntity<LeaderboardEntryDTO> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(leaderboardService.getByUsername(username));
    }
}