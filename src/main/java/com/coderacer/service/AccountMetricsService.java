package com.coderacer.service;

import com.coderacer.models.AccountMetrics;

import java.util.UUID;

public interface AccountMetricsService {
    AccountMetrics getAccountMetricsById(UUID id);
}
