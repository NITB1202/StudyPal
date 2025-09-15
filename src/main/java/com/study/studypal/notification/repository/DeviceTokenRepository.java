package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.DeviceToken;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
  DeviceToken findByUserIdAndToken(UUID userId, String token);
}
