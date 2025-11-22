package com.study.studypal.plan.service.internal.impl;

import com.study.studypal.plan.entity.TeamTaskCounter;
import com.study.studypal.plan.entity.UserTaskCounter;
import com.study.studypal.plan.repository.TeamTaskCounterRepository;
import com.study.studypal.plan.repository.UserTaskCounterRepository;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskCounterServiceImpl implements TaskCounterService {
  private final UserTaskCounterRepository userTaskCounterRepository;
  private final TeamTaskCounterRepository teamTaskCounterRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public void createUserTaskCounter(UUID userId) {
    User user = entityManager.getReference(User.class, userId);
    UserTaskCounter userTaskCounter = UserTaskCounter.builder().user(user).counter(0L).build();
    userTaskCounterRepository.save(userTaskCounter);
  }

  @Override
  public void createTeamTaskCounter(UUID teamId) {
    Team team = entityManager.getReference(Team.class, teamId);
    TeamTaskCounter teamTaskCounter = TeamTaskCounter.builder().team(team).counter(0L).build();
    teamTaskCounterRepository.save(teamTaskCounter);
  }
}
