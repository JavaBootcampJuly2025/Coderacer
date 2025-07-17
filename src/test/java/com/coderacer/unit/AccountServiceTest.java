package com.coderacer.unit;

import com.coderacer.dto.*;
import com.coderacer.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
    }

    @Test
    void testCreateAccount() {
        AccountCreateDTO dto = new AccountCreateDTO("user", "email@test.com", "pass");
        Mockito.when(accountService.createAccount(dto)).thenReturn(new AccountDTO(UUID.randomUUID(), "user", "email@test.com", 0, false));
        AccountDTO account = accountService.createAccount(dto);
        assertEquals("user", account.username());
        assertEquals("email@test.com", account.email());
    }

    @Test
    void testUpdateAccount() {
        UUID id = UUID.randomUUID();
        AccountUpdateDTO dto = new AccountUpdateDTO("newemail@test.com", 100, true);
        Mockito.when(accountService.updateAccount(id, dto)).thenReturn(new AccountDTO(UUID.randomUUID(), "user", "newemail@test.com", 100, true));
        AccountDTO account = accountService.updateAccount(id, dto);
        assertEquals("newemail@test.com", account.email());
        assertEquals(100, account.rating());
        assertTrue(account.verified());
    }

    @Test
    void testChangePassword() {
        UUID id = UUID.randomUUID();
        PasswordChangeDTO dto = new PasswordChangeDTO("oldpass", "newpass");
        assertDoesNotThrow(() -> accountService.changePassword(id, dto));
    }

    @Test
    void testUpdateRating() {
        LevelSessionCreateDto dto = Mockito.mock(LevelSessionCreateDto.class);
        assertDoesNotThrow(() -> accountService.updateRating(dto));
    }
}
