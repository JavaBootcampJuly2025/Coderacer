package com.coderacer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameplayMetricsDTO {
    private Double avgCpm;
    private Double avgAccuracy;
}