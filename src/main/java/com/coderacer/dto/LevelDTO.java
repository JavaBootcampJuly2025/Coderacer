package com.coderacer.dto;

import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;

import java.util.List;
import java.util.UUID;

public record LevelDTO(
        UUID id,
        String codeSnippet,
        ProgrammingLanguage language,
        Difficulty difficulty,
        List<String> tags
) {
    public static LevelDTO fromEntity(com.coderacer.model.Level level) {
        return new LevelDTO(
                level.getId(),
                level.getCodeSnippet(),
                level.getLanguage(),
                level.getDifficulty(),
                level.getTags()
        );
    }
}