package com.study.studypal.repositories;

import com.study.studypal.entities.TeamUser;
import com.study.studypal.entities.TeamUserId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeamUserRepository extends JpaRepository<TeamUser, TeamUserId> {
    boolean existsByUserIdAndTeamId(UUID userId, UUID teamId);
    @Query("""
    SELECT tu FROM TeamUser tu
    WHERE tu.team.id = :teamId
    ORDER BY tu.rolePriority ASC, tu.user.name ASC, tu.user.id ASC
    """)
    List<TeamUser> findTeamMembers(@Param("teamId") UUID teamId, Pageable pageable);
    @Query("""
    SELECT tu FROM TeamUser tu
    WHERE tu.team.id = :teamId
      AND (
        tu.rolePriority > :rolePriority
        OR (tu.rolePriority = :rolePriority AND tu.user.name > :name)
        OR (tu.rolePriority = :rolePriority AND tu.user.name = :name AND tu.user.id > :userId)
      )
    ORDER BY tu.rolePriority ASC, tu.user.name ASC, tu.user.id ASC
    """)
    List<TeamUser> findTeamMembersWithCursor(@Param("teamId") UUID teamId,
                                             @Param("rolePriority") int rolePriority,
                                             @Param("name") String name,
                                             @Param("userId") UUID userId,
                                             Pageable pageable);
    @Query("""
    SELECT t.totalMembers FROM Team t
    WHERE t.id = :teamId
    """)
    int getTotalMembers(@Param("teamId") UUID teamId);
    @Query("""
    SELECT tu FROM TeamUser tu
    WHERE tu.team.id = :teamId
        AND LOWER(tu.user.name) LIKE CONCAT('%', :keyword, '%')
        AND tu.user.id <> :userId
        AND (:cursor IS NULL OR tu.user.id > :cursor)
    ORDER BY tu.user.id ASC
    """)
    List<TeamUser> searchTeamMembersWithCursor(@Param("userId") UUID userId,
                                               @Param("teamId") UUID teamId,
                                               @Param("keyword") String keyword,
                                               @Param("cursor") UUID cursor,
                                               Pageable pageable);
    @Query("""
    SELECT COUNT(tu) FROM TeamUser tu
    WHERE tu.team.id = :teamId
        AND LOWER(tu.user.name) LIKE CONCAT('%', :keyword, '%')
        AND tu.user.id <> :userId
    """)
    long countTeamMembersByName(@Param("userId") UUID userId,
                                @Param("teamId") UUID teamId,
                                @Param("keyword") String keyword);
}