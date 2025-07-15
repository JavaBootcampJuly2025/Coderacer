package com.coderacer.dto;

import java.util.UUID;

public record AccountCreateDTO (
        String username,
        String email,
        String password
        /// NOT SURE IF THIS IS IT HERE
){}
