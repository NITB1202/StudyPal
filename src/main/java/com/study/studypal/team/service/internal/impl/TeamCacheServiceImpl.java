package com.study.studypal.team.service.internal.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.team.service.internal.TeamCacheService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamCacheServiceImpl implements TeamCacheService {
  private final CacheManager cacheManager;

  @Override
  public void evictTeamOverviewCaches(UUID teamId, List<UUID> memberIds) {
    Cache cache = cacheManager.getCache(CacheNames.TEAM_OVERVIEW);
    for (UUID memberId : memberIds) {
      Objects.requireNonNull(cache).evictIfPresent(CacheKeyUtils.of(memberId, teamId));
    }
  }

  @Override
  public void evictUserJoinedTeamsCaches(List<UUID> memberIds) {
    Cache cache = cacheManager.getCache(CacheNames.USER_TEAMS);
    for (UUID memberId : memberIds) {
      Objects.requireNonNull(cache).evictIfPresent(CacheKeyUtils.of(memberId));
    }
  }
}
