package com.study.studypal.team.event;

import com.study.studypal.team.event.team.TeamDeletedEvent;
import com.study.studypal.team.event.team.TeamUpdatedEvent;
import com.study.studypal.team.service.internal.TeamCacheService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamEventListener {
  private final TeamCacheService teamCacheService;
  private final TeamMembershipInternalService memberService;

  @Async
  @EventListener
  public void handleTeamUpdatedEvent(TeamUpdatedEvent event) {
    // Evict the user's joined team cache only if the team's name or the team's avatar has changed
    List<UUID> memberIds = memberService.getMemberIds(event.getTeamId());
    if (event.isShouldEvictCache()) {
      teamCacheService.evictUserJoinedTeamsCaches(memberIds);
    }
    teamCacheService.evictTeamDashboardCaches(event.getTeamId(), memberIds);
  }

  @Async
  @EventListener
  public void handleTeamDeleted(TeamDeletedEvent event) {
    teamCacheService.evictTeamDashboardCaches(event.getTeamId(), event.getMemberIds());
    teamCacheService.evictUserJoinedTeamsCaches(event.getMemberIds());
  }
}
