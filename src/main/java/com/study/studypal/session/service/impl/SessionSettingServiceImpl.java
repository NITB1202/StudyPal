package com.study.studypal.session.service.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.session.dto.setting.request.UpdateSessionSettingRequestDto;
import com.study.studypal.session.dto.setting.response.SessionSettingResponseDto;
import com.study.studypal.session.entity.SessionSetting;
import com.study.studypal.session.exception.SessionSettingErrorCode;
import com.study.studypal.session.repository.SessionSettingRepository;
import com.study.studypal.session.service.SessionSettingService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionSettingServiceImpl implements SessionSettingService {
  private final SessionSettingRepository settingRepository;
  private final ModelMapper modelMapper;

  @Override
  public SessionSettingResponseDto getSessionSetting(UUID userId) {
    SessionSetting setting =
        settingRepository
            .findById(userId)
            .orElseThrow(() -> new BaseException(SessionSettingErrorCode.SETTING_NOT_FOUND));
    return modelMapper.map(setting, SessionSettingResponseDto.class);
  }

  @Override
  public SessionSettingResponseDto updateSessionSetting(
      UUID userId, UpdateSessionSettingRequestDto request) {
    SessionSetting setting =
        settingRepository
            .findById(userId)
            .orElseThrow(() -> new BaseException(SessionSettingErrorCode.SETTING_NOT_FOUND));

    if (request.getTotalTimeInSeconds()
        < request.getFocusTimeInSeconds() + request.getBreakTimeInSeconds()) {
      throw new BaseException(SessionSettingErrorCode.INVALID_TOTAL_TIME);
    }

    modelMapper.map(request, setting);
    settingRepository.save(setting);

    return modelMapper.map(setting, SessionSettingResponseDto.class);
  }
}
