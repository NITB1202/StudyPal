package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.TeamNotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamNotificationSettingsRepository extends JpaRepository<TeamNotificationSettings, UUID> {
}