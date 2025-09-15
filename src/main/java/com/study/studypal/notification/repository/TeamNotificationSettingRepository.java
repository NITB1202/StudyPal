package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.TeamNotificationSetting;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamNotificationSettingRepository
    extends JpaRepository<TeamNotificationSetting, UUID> {
  @Query(
      """
    SELECT st
    FROM TeamNotificationSetting st
    JOIN st.membership tu
    WHERE tu.user.id = :userId
    AND tu.team.id = :teamId
    """)
  Optional<TeamNotificationSetting> findByUserIdAndTeamId(
      @Param("userId") UUID userId, @Param("teamId") UUID teamId);

  @Query(
      """
    SELECT u.id
    FROM TeamNotificationSetting st
    JOIN st.membership m
    JOIN m.user u
    WHERE st.id = :id
    """)
  UUID getUserIdById(@Param("id") UUID id);
}
