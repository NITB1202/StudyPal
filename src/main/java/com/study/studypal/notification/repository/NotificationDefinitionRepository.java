package com.study.studypal.notification.repository;

import com.study.studypal.notification.entity.NotificationDefinition;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDefinitionRepository
    extends JpaRepository<NotificationDefinition, UUID> {
  Optional<NotificationDefinition> findByCode(String code);
}
