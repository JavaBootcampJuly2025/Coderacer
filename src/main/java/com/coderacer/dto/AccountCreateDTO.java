package com.coderacer.dto;

import jakarta.validation.constraints.*;

public record AccountCreateDTO(
        @NotBlank @Size(min = 4, max = 25)
        @Pattern(regexp = "^[a-zA-Z0-9_]+$")
        String username,

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, max = 100)
        String password
) {}