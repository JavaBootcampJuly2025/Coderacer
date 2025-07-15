package com.coderacer.dto;

import com.coderacer.model.Account;

import java.util.UUID;

public record AccountDTO(
    UUID id,
    String username,
    String email,
    int rating
    ){
        public static AccountDTO fromEntity(Account account){
            return new AccountDTO(
                    account.getId(),
                    account.getUsername(),
                    account.getEmail(),
                    account.getRating()
            );
        }

}
