package com.study.studypal.repositories;

import com.study.studypal.entities.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    boolean existsByNameAndCreatorId(String name, UUID creatorId);
    Team findByTeamCode(String teamCode);
    boolean existsByTeamCode(String teamCode);
    @Query("""
    SELECT t
    FROM Team t JOIN TeamUser tu ON t.id = tu.teamId
    WHERE tu.userId = :userId
      AND (:cursor IS NULL OR tu.joinedAt < :cursor)
    ORDER BY tu.joinedAt DESC
    """)
    List<Team> findUserJoinedTeamWithCursor(@Param("userId") UUID userId,
                                            @Param("cursor") LocalDateTime cursor,
                                            Pageable pageable);
    @Query("""
    SELECT COUNT(t)
    FROM Team t JOIN TeamUser tu ON t.id = tu.teamId
    WHERE tu.userId = :userId
    """)
    long countUserJoinedTeam(@Param("userId") UUID userId);
    @Query("""
    SELECT t
    FROM Team t JOIN TeamUser tu ON t.id = tu.teamId
    WHERE tu.userId = :userId
      AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
      AND (:cursor IS NULL OR tu.joinedAt < :cursor)
    ORDER BY tu.joinedAt DESC
    """)
    List<Team> searchUserJoinedTeamByNameWithCursor(@Param("userId") UUID userId,
                                                    @Param("keyword") String keyword,
                                                    @Param("cursor") LocalDateTime cursor,
                                                    Pageable pageable);
    @Query("""
    SELECT COUNT(t)
    FROM Team t JOIN TeamUser tu ON t.id = tu.teamId
    WHERE tu.userId = :userId
        AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    """)
    long countUserJoinedTeamByName(@Param("userId") UUID userId,
                                   @Param("keyword") String keyword);

}