package com.coderacer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountUpdateDTO(
        @NotBlank @Size(min = 3, max = 50) String username,
        @Email String email,
        @Size(min = 8, max = 100) String hashedPassword,
        Integer rating,
        Boolean verified
) {}
