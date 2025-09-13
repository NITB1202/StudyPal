package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.TeamNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TeamNotificationSettingsRepository extends JpaRepository<TeamNotificationSettings, UUID> {
    @Query("""
    SELECT st FROM TeamNotificationSettings st
    JOIN st.membership tu
    WHERE tu.user.id = :userId
    AND tu.team.id = :teamId
    """)
    Optional<TeamNotificationSettings> findByUserIdAndTeamId(@Param("userId")UUID userId, @Param("teamId") UUID teamId);
}