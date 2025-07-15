package com.coderacer.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class AccountMetricsResponseDTO {
    private double avgCpm;
    private double avgAccuracy;
    private double avgCpmWeekly;
    private double avgAccuracyWeekly;
}
