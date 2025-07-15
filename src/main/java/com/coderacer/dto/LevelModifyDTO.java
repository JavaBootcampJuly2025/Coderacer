package com.coderacer.dto;

import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LevelModifyDTO(
        @NotBlank @Size(min = 100, max = 2000) String codeSnippet,
        @NotNull ProgrammingLanguage language,
        @NotNull Difficulty difficulty,
        List<@NotBlank @Size(max = 20) String> tags
) {}