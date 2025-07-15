package com.coderacer.dto;

import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionLookupParametersDTO {
    private ProgrammingLanguage language;
    private Difficulty difficulty;
    private LocalDateTime time;
}
