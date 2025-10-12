package com.study.studypal.team.repository;

import com.study.studypal.team.dto.team.response.TeamSummaryResponseDto;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.enums.TeamRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, UUID> {
  boolean existsByNameAndCreatorId(String name, UUID creatorId);

  Optional<Team> findByTeamCode(String teamCode);

  boolean existsByTeamCode(String teamCode);

  @Query(
      """
    SELECT COUNT(tu)
    FROM TeamUser tu
    WHERE tu.user.id = :userId
    AND tu.role = com.study.studypal.team.enums.TeamRole.OWNER
    """)
  long countUserOwnedTeams(@Param("userId") UUID userId);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = com.study.studypal.team.enums.TeamRole.OWNER THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> findUserJoinedTeams(@Param("userId") UUID userId, Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = com.study.studypal.team.enums.TeamRole.OWNER THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.joinedAt < :cursor
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> findUserJoinedTeamsWithCursor(
      @Param("userId") UUID userId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = com.study.studypal.team.enums.TeamRole.OWNER THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.role = :role
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> findUserTeamsWithRole(
      @Param("userId") UUID userId, @Param("role") TeamRole role, Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = com.study.studypal.team.enums.TeamRole.OWNER THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.role = :role
    AND tu.joinedAt < :cursor
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> findUserTeamsWithRoleAndCursor(
      @Param("userId") UUID userId,
      @Param("role") TeamRole role,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(t)
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    """)
  long countUserJoinedTeam(@Param("userId") UUID userId);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = com.study.studypal.team.enums.TeamRole.OWNER THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> searchUserJoinedTeamByName(
      @Param("userId") UUID userId, @Param("keyword") String keyword, Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = com.study.studypal.team.enums.TeamRole.OWNER THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    AND tu.joinedAt < :cursor
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> searchUserJoinedTeamByNameWithCursor(
      @Param("userId") UUID userId,
      @Param("keyword") String keyword,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(t)
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    """)
  long countUserJoinedTeamByName(@Param("userId") UUID userId, @Param("keyword") String keyword);
}
