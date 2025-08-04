package com.study.studypal.team.repository;

import com.study.studypal.team.dto.Team.response.TeamSummaryResponseDto;
import com.study.studypal.team.entity.Team;
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
    SELECT new com.study.studypal.team.dto.Team.response.TeamSummaryResponseDto(
        t.id,
        t.name,
        t.avatarUrl,
        CASE WHEN tu.role IN (com.study.studypal.team.enums.TeamRole.CREATOR, com.study.studypal.team.enums.TeamRole.ADMIN) THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
      AND (:cursor IS NULL OR tu.joinedAt < :cursor)
    ORDER BY tu.joinedAt DESC
""")
    List<TeamSummaryResponseDto> findUserJoinedTeamWithCursor(@Param("userId") UUID userId,
                                                              @Param("cursor") LocalDateTime cursor,
                                                              Pageable pageable);
    @Query("""
    SELECT COUNT(t)
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    """)
    long countUserJoinedTeam(@Param("userId") UUID userId);
    @Query("""
    SELECT new com.study.studypal.team.dto.Team.response.TeamSummaryResponseDto(
        t.id,
        t.name,
        t.avatarUrl,
        CASE WHEN tu.role IN (com.study.studypal.team.enums.TeamRole.CREATOR, com.study.studypal.team.enums.TeamRole.ADMIN) THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
      AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
      AND (:cursor IS NULL OR tu.joinedAt < :cursor)
    ORDER BY tu.joinedAt DESC
    """)
    List<TeamSummaryResponseDto> searchUserJoinedTeamByNameWithCursor(@Param("userId") UUID userId,
                                                    @Param("keyword") String keyword,
                                                    @Param("cursor") LocalDateTime cursor,
                                                    Pageable pageable);
    @Query("""
    SELECT COUNT(t)
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
        AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    """)
    long countUserJoinedTeamByName(@Param("userId") UUID userId,
                                   @Param("keyword") String keyword);

}