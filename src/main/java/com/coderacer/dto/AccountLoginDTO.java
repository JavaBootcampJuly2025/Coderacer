package com.coderacer.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountLoginDTO {
    @NotNull
    @Column(length = 25, nullable = false)
    @NotBlank(message = "Username cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores")
    String username;

    @NotBlank @Size(min = 8, max = 100)
    String password;
}
