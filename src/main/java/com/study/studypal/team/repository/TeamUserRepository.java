package com.study.studypal.team.repository;

import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser, UUID> {
  boolean existsByUserIdAndTeamId(UUID userId, UUID teamId);

  Optional<TeamUser> findByUserIdAndTeamId(UUID userId, UUID teamId);

  @Query(
      """
    SELECT tu FROM TeamUser tu
    WHERE tu.team.id = :teamId
    ORDER BY tu.rolePriority ASC, tu.user.name ASC, tu.user.id ASC
    """)
  List<TeamUser> findTeamMembers(@Param("teamId") UUID teamId, Pageable pageable);

  @Query(
      """
    SELECT tu FROM TeamUser tu
    WHERE tu.team.id = :teamId
      AND (
        tu.rolePriority > :rolePriority
        OR (tu.rolePriority = :rolePriority AND tu.user.name > :name)
        OR (tu.rolePriority = :rolePriority AND tu.user.name = :name AND tu.user.id > :userId)
      )
    ORDER BY tu.rolePriority ASC, tu.user.name ASC, tu.user.id ASC
    """)
  List<TeamUser> findTeamMembersWithCursor(
      @Param("teamId") UUID teamId,
      @Param("rolePriority") int rolePriority,
      @Param("name") String name,
      @Param("userId") UUID userId,
      Pageable pageable);

  @Query("""
    SELECT t.totalMembers FROM Team t
    WHERE t.id = :teamId
    """)
  int getTotalMembers(@Param("teamId") UUID teamId);

  @Query(
      """
    SELECT tu FROM TeamUser tu
    WHERE tu.team.id = :teamId
        AND LOWER(tu.user.name) LIKE CONCAT('%', :keyword, '%')
        AND tu.user.id <> :userId
        AND (:cursor IS NULL OR tu.user.id > :cursor)
    ORDER BY tu.user.id ASC
    """)
  List<TeamUser> searchTeamMembersWithCursor(
      @Param("userId") UUID userId,
      @Param("teamId") UUID teamId,
      @Param("keyword") String keyword,
      @Param("cursor") UUID cursor,
      Pageable pageable);

  @Query(
      """
    SELECT COUNT(tu) FROM TeamUser tu
    WHERE tu.team.id = :teamId
        AND LOWER(tu.user.name) LIKE CONCAT('%', :keyword, '%')
        AND tu.user.id <> :userId
    """)
  long countTeamMembersByName(
      @Param("userId") UUID userId, @Param("teamId") UUID teamId, @Param("keyword") String keyword);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT tu FROM TeamUser tu WHERE tu.user.id = :userId AND tu.team.id = :teamId")
  Optional<TeamUser> findByUserIdAndTeamIdForUpdate(UUID userId, UUID teamId);

  // Custom delete query that returns affected rows for race condition check
  @Modifying
  @Query("DELETE FROM TeamUser tu WHERE tu.id = :id")
  int deleteMemberById(UUID id);

  @Query("""
    SELECT tu.user.id FROM TeamUser tu
    WHERE tu.team.id = :teamId
    """)
  List<UUID> getTeamMemberUserIds(@Param("teamId") UUID teamId);

  @Query(
      """
    SELECT new com.study.studypal.user.dto.internal.UserSummaryProfile(
      tu.user.name,
      tu.user.avatarUrl
    )
    FROM TeamUser tu
    WHERE tu.team.id = :teamId
    AND tu.role = com.study.studypal.team.enums.TeamRole.OWNER
    """)
  UserSummaryProfile getTeamOwner(@Param("teamId") UUID teamId);
}
