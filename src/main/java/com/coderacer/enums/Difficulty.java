package com.coderacer.enums;

import lombok.Getter;

@Getter
public enum Difficulty {
    EASY(0.5),
    MEDIUM(1.0),
    HARD(1.5);

    private final double multiplier;

    Difficulty(double multiplier) {
        this.multiplier = multiplier;
    }
}