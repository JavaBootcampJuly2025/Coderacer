package com.coderacer.service;

import com.coderacer.dto.AccountMetricsResponseDTO;

import java.util.UUID;

public interface AccountMetricsService {
    AccountMetricsResponseDTO getAccountMetricsByAccountId(UUID id);
}
