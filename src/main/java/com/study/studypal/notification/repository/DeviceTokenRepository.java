package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.DeviceToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
  DeviceToken findByUserIdAndToken(UUID userId, String token);

  void deleteAllByLastUpdatedBefore(LocalDateTime time);

  List<DeviceToken> findByUserId(UUID userId);
}
