package com.coderacer.unit;

import com.coderacer.dto.LeaderboardEntryDTO;
import com.coderacer.service.LeaderboardEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardEntryServiceTest {
    private LeaderboardEntryService leaderboardEntryService;

    @BeforeEach
    void setUp() {
        leaderboardEntryService = Mockito.mock(LeaderboardEntryService.class);
    }

    @Test
    void testGetTop10() {
        Mockito.when(leaderboardEntryService.getTop10()).thenReturn(List.of(new LeaderboardEntryDTO(UUID.randomUUID(), "user", 100)));
        List<LeaderboardEntryDTO> top10 = leaderboardEntryService.getTop10();
        assertFalse(top10.isEmpty());
        assertEquals("user", top10.get(0).username());
    }

    @Test
    void testGetLeaderboard() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<LeaderboardEntryDTO> page = new PageImpl<>(List.of(new LeaderboardEntryDTO(UUID.randomUUID(), "user", 100)), pageable, 1);
        Mockito.when(leaderboardEntryService.getLeaderboard(pageable)).thenReturn(page);
        Page<LeaderboardEntryDTO> result = leaderboardEntryService.getLeaderboard(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetByUsername() {
        Mockito.when(leaderboardEntryService.getByUsername("user")).thenReturn(new LeaderboardEntryDTO(UUID.randomUUID(), "user", 100));
        LeaderboardEntryDTO entry = leaderboardEntryService.getByUsername("user");
        assertEquals("user", entry.username());
        assertEquals(100, entry.matchmakingRating());
    }
}
