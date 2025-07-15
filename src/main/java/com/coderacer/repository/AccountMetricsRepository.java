package com.coderacer.repository;

import com.coderacer.models.AccountMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountMetricsRepository extends JpaRepository<AccountMetrics, UUID> {
    AccountMetrics findAccountMetricsByAccountId(UUID id);
}

