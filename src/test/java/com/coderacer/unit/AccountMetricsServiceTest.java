package com.coderacer.unit;

import com.coderacer.dto.GameplayMetricsDTO;
import com.coderacer.dto.SessionLookupParametersDTO;
import com.coderacer.repository.LevelSessionRepository;
import com.coderacer.service.AccountMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountMetricsServiceTest {
    private AccountMetricsService accountMetricsService;
    private LevelSessionRepository levelSessionRepository;

    @BeforeEach
    void setUp() {
        levelSessionRepository = Mockito.mock(LevelSessionRepository.class);
        accountMetricsService = new AccountMetricsService(levelSessionRepository);
    }

    @Test
    void testGetAccountMetrics() {
        UUID id = UUID.randomUUID();
        GameplayMetricsDTO metrics = Mockito.mock(GameplayMetricsDTO.class);
        Mockito.when(levelSessionRepository.findAvgMetricsByAccountId(id)).thenReturn(metrics);
        assertEquals(metrics, accountMetricsService.getAccountMetrics(id));
    }

    @Test
    void testGetAccountMetricsWithParameters() {
        UUID id = UUID.randomUUID();
        SessionLookupParametersDTO params = Mockito.mock(SessionLookupParametersDTO.class);
        GameplayMetricsDTO metrics = Mockito.mock(GameplayMetricsDTO.class);
        Mockito.when(levelSessionRepository.findAvgMetricsByAccountIdWithParameters(id, params)).thenReturn(metrics);
        assertEquals(metrics, accountMetricsService.getAccountMetrics(id, params));
    }
}
