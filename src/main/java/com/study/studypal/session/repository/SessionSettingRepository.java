package com.study.studypal.session.repository;

import com.study.studypal.session.entity.SessionSetting;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionSettingRepository extends JpaRepository<SessionSetting, UUID> {}
