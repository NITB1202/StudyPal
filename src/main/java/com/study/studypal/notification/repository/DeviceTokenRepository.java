package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
}