package com.study.studypal.team.event;

import com.study.studypal.team.event.team.TeamCodeResetEvent;
import com.study.studypal.team.event.team.TeamDeletedEvent;
import com.study.studypal.team.event.team.TeamUpdatedEvent;
import com.study.studypal.team.service.internal.TeamCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TeamEventListener {
  private final TeamCacheService teamCacheService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleTeamUpdatedEvent(TeamUpdatedEvent event) {
    // Evict the user's joined team cache only if the team's name or the team's avatar has changed
    if (event.isShouldEvictCache()) {
      teamCacheService.evictUserJoinedTeamsCaches(event.getMemberIds());
    }
    teamCacheService.evictTeamOverviewCaches(event.getTeamId(), event.getMemberIds());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleTeamCodeResetEvent(TeamCodeResetEvent event) {
    teamCacheService.evictTeamOverviewCaches(event.getTeamId(), event.getMemberIds());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleTeamDeleted(TeamDeletedEvent event) {
    teamCacheService.evictTeamOverviewCaches(event.getTeamId(), event.getMemberIds());
    teamCacheService.evictUserJoinedTeamsCaches(event.getMemberIds());
  }
}
