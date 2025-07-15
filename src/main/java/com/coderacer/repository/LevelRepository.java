package com.coderacer.repository;

import com.coderacer.enums.Difficulty;
import com.coderacer.enums.ProgrammingLanguage;
import com.coderacer.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LevelRepository extends JpaRepository<Level, UUID> {
    List<Level> findByLanguage(ProgrammingLanguage language);
    List<Level> findByDifficulty(Difficulty difficulty);
    List<Level> findByLanguageAndDifficulty(ProgrammingLanguage language, Difficulty difficulty);
    List<Level> findByTagsContaining(String tag);
    List<Level> findByTagsIn(List<String> tags);
    long countByDifficulty(Difficulty difficulty);
}