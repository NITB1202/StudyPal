package com.study.studypal.team.service.internal.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.exception.TeamMembershipErrorCode;
import com.study.studypal.team.repository.TeamUserRepository;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMembershipInternalServiceImpl implements TeamMembershipInternalService {
    private final TeamUserRepository teamUserRepository;
    private final CacheManager cacheManager;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void createMembership(UUID teamId, UUID userId, TeamRole role) {
        Team team = entityManager.getReference(Team.class, teamId);
        User user = entityManager.getReference(User.class, userId);

        TeamUser membership = TeamUser.builder()
                .team(team)
                .user(user)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();

        teamUserRepository.save(membership);
    }

    @Override
    public void validateUpdateTeamPermission(UUID userId, UUID teamId) {
        TeamUser membership = teamUserRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                ()-> new BaseException(TeamMembershipErrorCode.USER_MEMBERSHIP_NOT_FOUND)
        );

        if(membership.getRole() != TeamRole.CREATOR) {
            throw new BaseException(TeamMembershipErrorCode.PERMISSION_UPDATE_TEAM_DENIED);
        }
    }

    @Override
    public void validateInviteMemberPermission(UUID userId, UUID teamId, UUID inviteeId) {
        TeamUser membership = getMemberShip(teamId, userId);

        if(membership.getRole() == TeamRole.MEMBER) {
            throw new BaseException(TeamMembershipErrorCode.PERMISSION_INVITE_MEMBER_DENIED);
        }

        if(teamUserRepository.existsByUserIdAndTeamId(inviteeId, teamId)) {
            throw new BaseException(TeamMembershipErrorCode.INVITEE_ALREADY_IN_TEAM);
        }
    }

    @Override
    public TeamUser getMemberShip(UUID teamId, UUID userId) {
        return teamUserRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                ()-> new BaseException(TeamMembershipErrorCode.TARGET_MEMBERSHIP_NOT_FOUND)
        );
    }

    @Override
    public LocalDateTime getUserJoinedTeamsListCursor(UUID userId, UUID lastTeamId, int listSize, int size) {
        TeamUser membership = getMemberShip(lastTeamId, userId);
        return listSize > 0 && listSize == size ? membership.getJoinedAt() : null;
    }

    @Async
    @Override
    public void evictTeamOverviewCaches(UUID teamId) {
        log.info("Start evictTeamOverviewCaches for teamId {}", teamId);
        Cache cache = cacheManager.getCache(CacheNames.TEAM_OVERVIEW);
        List<UUID> memberIds = teamUserRepository.getTeamMemberUserIds(teamId);
        for(UUID memberId : memberIds) {
            Objects.requireNonNull(cache).evictIfPresent(CacheKeyUtils.of(memberId, teamId));
        }
        log.info("End evictTeamOverviewCaches for teamId {}", teamId);
    }

    @Async
    @Override
    public void evictUserJoinedTeamsCaches(UUID teamId) {
        Cache cache = cacheManager.getCache(CacheNames.USER_TEAMS);
        List<UUID> memberIds = teamUserRepository.getTeamMemberUserIds(teamId);
        for(UUID memberId : memberIds) {
            Objects.requireNonNull(cache).evictIfPresent(CacheKeyUtils.of(memberId));
        }
    }
}
