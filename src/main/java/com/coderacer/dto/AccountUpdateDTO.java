// AccountUpdateDTO.java
package com.coderacer.dto;

import jakarta.validation.constraints.*;

public record AccountUpdateDTO(
        @Email
        String email,

        @Min(0)
        Integer rating,

        Boolean verified
) {}