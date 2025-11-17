package com.study.studypal.team.repository;

import com.study.studypal.team.dto.team.response.TeamSummaryResponseDto;
import com.study.studypal.team.entity.Team;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
  boolean existsByNameAndCreatorId(String name, UUID creatorId);

  Optional<Team> findByTeamCode(String teamCode);

  boolean existsByTeamCode(String teamCode);

  @Query(
      """
    SELECT COUNT(tu)
    FROM TeamUser tu
    WHERE tu.user.id = :userId
    AND tu.role = 'OWNER'
    """)
  long countUserOwnedTeams(@Param("userId") UUID userId);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = 'OWNER' THEN true ELSE false END
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
    CASE WHEN tu.role = 'OWNER' THEN true ELSE false END
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
    true
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.role = 'OWNER'
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> findUserOwnedTeams(@Param("userId") UUID userId, Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    true
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.role = 'OWNER'
    AND tu.joinedAt < :cursor
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> findUserOwnedTeamsWithCursor(
      @Param("userId") UUID userId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

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
    CASE WHEN tu.role = 'OWNER' THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> searchUserJoinedTeamsByName(
      @Param("userId") UUID userId, @Param("keyword") String keyword, Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    CASE WHEN tu.role = 'OWNER' THEN true ELSE false END
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    AND tu.joinedAt < :cursor
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> searchUserJoinedTeamsByNameWithCursor(
      @Param("userId") UUID userId,
      @Param("keyword") String keyword,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    true
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.role = 'OWNER'
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> searchUserOwnedTeamsByName(
      @Param("userId") UUID userId, @Param("keyword") String keyword, Pageable pageable);

  @Query(
      """
    SELECT new com.study.studypal.team.dto.team.response.TeamSummaryResponseDto(
    t.id,
    t.name,
    t.avatarUrl,
    true
    )
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.role = 'OWNER'
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    AND tu.joinedAt < :cursor
    ORDER BY tu.joinedAt DESC
    """)
  List<TeamSummaryResponseDto> searchUserOwnedTeamsByNameWithCursor(
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

  @Query(
      """
    SELECT COUNT(t)
    FROM Team t JOIN TeamUser tu ON t.id = tu.team.id
    WHERE tu.user.id = :userId
    AND tu.role = 'OWNER'
    AND LOWER(t.name) LIKE CONCAT('%', :keyword, '%')
    """)
  long countUserOwnedTeamByName(@Param("userId") UUID userId, @Param("keyword") String keyword);
}
