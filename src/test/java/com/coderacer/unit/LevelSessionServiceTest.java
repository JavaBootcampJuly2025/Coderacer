package com.coderacer.unit;

import com.coderacer.model.LevelSession;
import com.coderacer.repository.AccountRepository;
import com.coderacer.repository.LevelRepository;
import com.coderacer.repository.LevelSessionRepository;
import com.coderacer.service.LevelSessionService;
import com.coderacer.dto.LevelSessionCreateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class LevelSessionServiceTest {
    private LevelSessionService levelSessionService;
    private LevelSessionRepository levelSessionRepository;
    private LevelRepository levelRepository;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        levelSessionRepository = Mockito.mock(LevelSessionRepository.class);
        levelRepository = Mockito.mock(LevelRepository.class);
        accountRepository = Mockito.mock(AccountRepository.class);
        levelSessionService = new LevelSessionService(levelSessionRepository, levelRepository, accountRepository);
    }

    @Test
    void testCreateLevelSession() {
        LevelSessionCreateDto dto = Mockito.mock(LevelSessionCreateDto.class);
        UUID levelId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Mockito.when(dto.getLevelId()).thenReturn(levelId);
        Mockito.when(dto.getAccountId()).thenReturn(accountId);
        Mockito.when(dto.getCpm()).thenReturn(100.0);
        Mockito.when(dto.getAccuracy()).thenReturn(0.95);
        Mockito.when(dto.getStartTime()).thenReturn(java.time.LocalDateTime.now());
        Mockito.when(dto.getEndTime()).thenReturn(java.time.LocalDateTime.now().plusMinutes(1));
        // Mock level and account repository responses
        Mockito.when(levelRepository.findById(levelId)).thenReturn(Optional.of(Mockito.mock(com.coderacer.model.Level.class)));
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(Mockito.mock(com.coderacer.model.Account.class)));
        LevelSession session = Mockito.mock(LevelSession.class);
        Mockito.when(levelSessionRepository.save(Mockito.any())).thenReturn(session);
        assertNotNull(levelSessionService.createLevelSession(dto));
    }

    @Test
    void testGetLevelSessionById() {
        UUID id = UUID.randomUUID();
        LevelSession session = Mockito.mock(LevelSession.class);
        Mockito.when(levelSessionRepository.findById(id)).thenReturn(Optional.of(session));
        assertTrue(levelSessionService.getLevelSessionById(id).isPresent());
    }

    @Test
    void testGetLevelSessionsByAccount() {
        UUID accountId = UUID.randomUUID();
        Mockito.when(levelSessionRepository.findByAccountId(accountId)).thenReturn(List.of(Mockito.mock(LevelSession.class)));
        List<LevelSession> sessions = levelSessionService.getLevelSessionsByAccount(accountId);
        assertFalse(sessions.isEmpty());
    }

    @Test
    void testUpdateLevelSession() {
        UUID id = UUID.randomUUID();
        LevelSession existing = Mockito.mock(LevelSession.class);
        LevelSession updated = Mockito.mock(LevelSession.class);
        Mockito.when(levelSessionRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(levelSessionRepository.save(existing)).thenReturn(updated);
    }

    @Test
    void testDeleteLevelSession() {
        UUID id = UUID.randomUUID();
        Mockito.when(levelSessionRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> levelSessionService.deleteLevelSession(id));
    }
}
