package com.coderacer.controller;

import com.coderacer.dto.LevelDTO;
import com.coderacer.dto.LevelModifyDTO;
import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import com.coderacer.service.LevelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping("/{id}")
    public ResponseEntity<LevelDTO> getLevel(@PathVariable UUID id) {
        return ResponseEntity.ok(levelService.getLevel(id));
    }

    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels() {
        return ResponseEntity.ok(levelService.getAllLevels());
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<LevelDTO>> getByLanguage(@PathVariable ProgrammingLanguage language) {
        return ResponseEntity.ok(levelService.getLevelsByLanguage(language));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<LevelDTO>> getByDifficulty(@PathVariable Difficulty difficulty) {
        return ResponseEntity.ok(levelService.getLevelsByDifficulty(difficulty));
    }

    @GetMapping("/random")
    public ResponseEntity<LevelDTO> getRandomLevel(
            @RequestParam ProgrammingLanguage language,
            @RequestParam Difficulty difficulty) {
        return ResponseEntity.ok(levelService.getRandomLevel(language, difficulty));
    }

    @PostMapping
    public ResponseEntity<LevelDTO> createLevel(@RequestBody @Valid LevelModifyDTO dto) {
        LevelDTO created = levelService.createLevel(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LevelDTO> updateLevel(
            @PathVariable UUID id,
            @RequestBody @Valid LevelModifyDTO dto) {
        return ResponseEntity.ok(levelService.updateLevel(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevel(@PathVariable UUID id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }
}
