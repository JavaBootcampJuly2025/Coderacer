package com.coderacer.service;

import com.coderacer.dto.AccountMetricsDTO;
import com.coderacer.models.AccountMetrics;

import java.util.UUID;

public interface AccountMetricsService {
    AccountMetricsDTO getAccountMetricsByAccountId(UUID id);
}
