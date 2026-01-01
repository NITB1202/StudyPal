package com.study.studypal.session.service.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import com.study.studypal.session.dto.request.SaveSessionRequestDto;
import com.study.studypal.session.dto.response.SessionResponseDto;
import com.study.studypal.session.dto.response.SessionStatisticsResponseDto;
import com.study.studypal.session.entity.Session;
import com.study.studypal.session.repository.SessionRepository;
import com.study.studypal.session.service.SessionService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
  private final SessionRepository sessionRepository;
  private final ModelMapper modelMapper;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public SessionResponseDto saveSession(UUID userId, SaveSessionRequestDto request) {
    if (request.getElapsedTimeInSeconds() > request.getDurationInSeconds()) {
      throw new BaseException(CommonErrorCode.INVALID_TIME_RANGE);
    }

    User user = entityManager.getReference(User.class, userId);
    Session session = modelMapper.map(request, Session.class);
    session.setUser(user);

    sessionRepository.save(session);
    return modelMapper.map(session, SessionResponseDto.class);
  }

  @Override
  public SessionStatisticsResponseDto getSessionStatistics(
      UUID userId, LocalDateTime fromDate, LocalDateTime toDate) {
    List<Session> sessions =
        sessionRepository.findAllByUserIdAndStudiedAtBetween(userId, fromDate, toDate);

    int completedSessionCount = 0;
    long timeSpentInSeconds = 0;

    for (Session session : sessions) {
      if (session.getElapsedTimeInSeconds().equals(session.getDurationInSeconds())) {
        completedSessionCount++;
      }

      timeSpentInSeconds += session.getElapsedTimeInSeconds();
    }

    double completionPercentage =
        completedSessionCount != 0 ? (double) completedSessionCount / sessions.size() : 0;

    return SessionStatisticsResponseDto.builder()
        .timeSpentInSeconds(timeSpentInSeconds)
        .completionPercentage(completionPercentage)
        .build();
  }
}
