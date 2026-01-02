package com.study.studypal.session.service;

import com.study.studypal.session.dto.setting.request.UpdateSessionSettingRequestDto;
import com.study.studypal.session.dto.setting.response.SessionSettingResponseDto;
import com.study.studypal.session.dto.setting.response.SystemMusicResponseDto;
import java.util.List;
import java.util.UUID;

public interface SessionSettingService {
  SessionSettingResponseDto getSessionSetting(UUID userId);

  SessionSettingResponseDto updateSessionSetting(
      UUID userId, UpdateSessionSettingRequestDto request);

  List<SystemMusicResponseDto> getSystemMusics();
}
