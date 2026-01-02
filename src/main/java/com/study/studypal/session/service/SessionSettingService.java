package com.study.studypal.session.service;

import com.study.studypal.session.dto.setting.request.UpdateSessionSettingRequestDto;
import com.study.studypal.session.dto.setting.response.SessionSettingResponseDto;
import java.util.UUID;

public interface SessionSettingService {
  SessionSettingResponseDto getSessionSetting(UUID userId);

  SessionSettingResponseDto updateSessionSetting(
      UUID userId, UpdateSessionSettingRequestDto request);
}
