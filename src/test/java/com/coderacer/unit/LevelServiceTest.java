package com.coderacer.unit;

import com.coderacer.dto.LevelDTO;
import com.coderacer.dto.LevelModifyDTO;
import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import com.coderacer.service.LevelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LevelServiceTest {
    private LevelService levelService;

    @BeforeEach
    void setUp() {
        levelService = Mockito.mock(LevelService.class);
    }

    @Test
    void testGetLevel() {
        UUID id = UUID.randomUUID();
        Mockito.when(levelService.getLevel(id)).thenReturn(new LevelDTO(id, "code", ProgrammingLanguage.JAVA, Difficulty.EASY, List.of("tag")));
        LevelDTO dto = levelService.getLevel(id);
        assertEquals(id, dto.id());
    }

    @Test
    void testGetAllLevels() {
        Mockito.when(levelService.getAllLevels()).thenReturn(List.of(new LevelDTO(UUID.randomUUID(), "code", ProgrammingLanguage.JAVA, Difficulty.EASY, List.of("tag"))));
        List<LevelDTO> levels = levelService.getAllLevels();
        assertFalse(levels.isEmpty());
    }

    @Test
    void testCreateLevel() {
        LevelModifyDTO dto = new LevelModifyDTO("code", ProgrammingLanguage.JAVA, Difficulty.EASY, List.of("tag"));
        Mockito.when(levelService.createLevel(dto)).thenReturn(new LevelDTO(UUID.randomUUID(), "code", ProgrammingLanguage.JAVA, Difficulty.EASY, List.of("tag")));
        LevelDTO created = levelService.createLevel(dto);
        assertEquals("code", created.codeSnippet());
    }

    @Test
    void testDeleteLevel() {
        UUID id = UUID.randomUUID();
        assertDoesNotThrow(() -> levelService.deleteLevel(id));
    }
}
