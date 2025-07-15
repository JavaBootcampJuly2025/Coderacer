package com.coderacer.repository;

import com.coderacer.model.LevelSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface LevelSessionRepository extends JpaRepository<LevelSession, UUID> {
    List<LevelSession> findByAccountId(UUID accountId);
    List<LevelSession> findByLevelId(UUID levelId);
    List<LevelSession> findByLevelIdOrderByCpmDesc(UUID levelId);
    List<LevelSession> findByLevelIdOrderByAccuracyDesc(UUID levelId);
}
