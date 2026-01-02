package com.study.studypal.session.service.impl;

import com.study.studypal.session.config.SessionProperties;
import com.study.studypal.session.entity.SessionSetting;
import com.study.studypal.session.repository.SessionSettingRepository;
import com.study.studypal.session.service.SessionSettingInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionSettingInternalServiceImpl implements SessionSettingInternalService {
  private final SessionSettingRepository settingRepository;
  private final SessionProperties sessionProperties;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createDefaultSetting(UUID userId) {
    User user = entityManager.getReference(User.class, userId);

    SessionSetting setting =
        SessionSetting.builder()
            .user(user)
            .focusTimeInSeconds(sessionProperties.getDefaultFocusTimeInSeconds())
            .breakTimeInSeconds(sessionProperties.getDefaultBreakTimeInSeconds())
            .totalTimeInSeconds(sessionProperties.getDefaultTotalTimeInSeconds())
            .enableBgMusic(sessionProperties.getDefaultEnableBgMusic())
            .build();

    settingRepository.save(setting);
  }
}
