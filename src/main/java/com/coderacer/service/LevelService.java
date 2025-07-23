package com.coderacer.service;

import com.coderacer.dto.LevelDTO;
import com.coderacer.dto.LevelModifyDTO;
import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import com.coderacer.model.Level;
import com.coderacer.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    @Transactional(readOnly = true)
    public LevelDTO getLevel(UUID id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Level not found: " + id));
        return LevelDTO.fromEntity(level);
    }

    @Transactional(readOnly = true)
    public List<LevelDTO> getAllLevels() {
        return levelRepository.findAll().stream()
                .map(LevelDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LevelDTO> getLevelsByLanguage(ProgrammingLanguage language) {
        return levelRepository.findByLanguage(language).stream()
                .map(LevelDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LevelDTO> getLevelsByDifficulty(Difficulty difficulty) {
        return levelRepository.findByDifficulty(difficulty).stream()
                .map(LevelDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public LevelDTO getRandomLevel(ProgrammingLanguage language, Difficulty difficulty) {
        List<Level> list = levelRepository.findByLanguageAndDifficulty(language, difficulty);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No levels found for " + language + "/" + difficulty);
        }
        Level random = list.get(ThreadLocalRandom.current().nextInt(list.size()));
        return LevelDTO.fromEntity(random);
    }

    @Transactional(readOnly = true)
    public LevelDTO getRandomLevel(Difficulty difficulty) {
        List<Level> list = levelRepository.findByDifficulty(difficulty);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No levels found for " + difficulty);
        }
        Level random = list.get(ThreadLocalRandom.current().nextInt(list.size()));
        return LevelDTO.fromEntity(random);
    }

    @Transactional
    public LevelDTO createLevel(LevelModifyDTO dto) {
        Level level = new Level();
        level.setCodeSnippet(dto.codeSnippet());
        level.setLanguage(dto.language());
        level.setDifficulty(dto.difficulty());
        level.setTags(dto.tags());

        Level saved = levelRepository.save(level);
        return LevelDTO.fromEntity(saved);
    }

    @Transactional
    public LevelDTO updateLevel(UUID id, LevelModifyDTO dto) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Level not found: " + id));
        level.setCodeSnippet(dto.codeSnippet());
        level.setLanguage(dto.language());
        level.setDifficulty(dto.difficulty());
        level.setTags(dto.tags());

        Level saved = levelRepository.save(level);
        return LevelDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteLevel(UUID id) {
        if (!levelRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Level not found: " + id);
        }
        levelRepository.deleteById(id);
    }
}
