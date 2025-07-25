package com.coderacer.repository;

import com.coderacer.dto.GameplayMetricsDTO;
import com.coderacer.dto.SessionLookupParametersDTO;
import com.coderacer.model.LevelSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface LevelSessionRepository extends JpaRepository<LevelSession, UUID> {
    @Query("SELECT new com.coderacer.dto.GameplayMetricsDTO(AVG(ls.cpm), AVG(ls.accuracy)) " +
            "FROM LevelSession ls WHERE ls.account.id = :id")
    GameplayMetricsDTO findAvgMetricsByAccountId(@Param("id") UUID id);

    @Query("SELECT new com.coderacer.dto.GameplayMetricsDTO(AVG(ls.cpm), AVG(ls.accuracy)) " +
            "FROM LevelSession ls " +
            "WHERE ls.account.id = :id " +
            "AND ls.level.language = :#{#params.language} " +
            "AND ls.level.difficulty = :#{#params.difficulty} " +
            "AND ls.endTime >= :#{#params.time}")
    GameplayMetricsDTO findAvgMetricsByAccountIdWithParameters(
            @Param("id") UUID id,
            @Param("params") SessionLookupParametersDTO parameters
    );
    List<LevelSession> findByAccountId(UUID accountId);
    List<LevelSession> findByLevelId(UUID levelId);
    void deleteByAccountId(UUID accountId);
    void deleteByLevelId(UUID levelId);
    List<LevelSession> findByLevelIdOrderByCpmDesc(UUID levelId);
    List<LevelSession> findByLevelIdOrderByAccuracyDesc(UUID levelId);
}
