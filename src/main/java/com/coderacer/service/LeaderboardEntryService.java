package com.coderacer.service;

import com.coderacer.dto.LeaderboardEntryDTO;
import com.coderacer.model.LeaderboardEntry;
import com.coderacer.repository.LeaderboardEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardEntryService {

    private final LeaderboardEntryRepository leaderboardEntryRepository;

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDTO> getTop10() {
        return leaderboardEntryRepository.findTop10ByOrderByMatchmakingRatingDesc().stream()
                .map(LeaderboardEntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<LeaderboardEntryDTO> getLeaderboard(Pageable pageable) {
        List<LeaderboardEntryDTO> entries = leaderboardEntryRepository
                .findAllByOrderByMatchmakingRatingDesc(pageable).stream()
                .map(LeaderboardEntryDTO::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(entries, pageable, leaderboardEntryRepository.count());
    }

    @Transactional(readOnly = true)
    public LeaderboardEntryDTO getByUsername(String username) {
        Optional<LeaderboardEntry> entry = leaderboardEntryRepository.findByUsername(username);
        return entry.map(LeaderboardEntryDTO::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + username));
    }
}