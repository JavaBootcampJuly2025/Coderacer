package com.coderacer.service;

import com.coderacer.dto.GameplayMetricsDTO;
import com.coderacer.dto.SessionLookupParametersDTO;
import com.coderacer.repository.LevelSessionRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountMetricsService {
    private final LevelSessionRepository levelSessionRepository;

    public GameplayMetricsDTO getAccountMetrics(UUID id) {
        return levelSessionRepository.findAvgMetricsByAccountId(id);
    }

    public GameplayMetricsDTO getAccountMetrics(UUID id, SessionLookupParametersDTO parameters) {
        if (!(parameters.getTime() == LocalDateTime.MIN)) {
            Duration difference = Duration.between(parameters.getTime(), LocalDateTime.now());
            parameters.setTime(parameters.getTime().minus(difference));
        }
        return levelSessionRepository.findAvgMetricsByAccountIdWithParameters(id, parameters);
    }
}
