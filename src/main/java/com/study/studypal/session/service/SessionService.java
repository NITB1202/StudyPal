package com.study.studypal.session.service;

import com.study.studypal.session.dto.request.SaveSessionRequestDto;
import com.study.studypal.session.dto.response.SessionResponseDto;
import com.study.studypal.session.dto.response.SessionStatisticsResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

public interface SessionService {
  SessionResponseDto saveSession(UUID userId, SaveSessionRequestDto request);

  SessionStatisticsResponseDto getSessionStatistics(
      UUID userId, LocalDateTime fromDate, LocalDateTime toDate);
}
