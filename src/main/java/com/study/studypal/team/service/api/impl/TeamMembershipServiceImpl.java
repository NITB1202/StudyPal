package com.study.studypal.team.service.api.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.notification.service.internal.TeamNotificationSettingInternalService;
import com.study.studypal.plan.service.internal.TaskInternalService;
import com.study.studypal.team.config.TeamProperties;
import com.study.studypal.team.dto.membership.internal.DecodedCursor;
import com.study.studypal.team.dto.membership.request.RemoveTeamMemberRequestDto;
import com.study.studypal.team.dto.membership.request.UpdateMemberRoleRequestDto;
import com.study.studypal.team.dto.membership.response.ListTeamMemberResponseDto;
import com.study.studypal.team.dto.membership.response.TeamMemberResponseDto;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.event.team.UserJoinedTeamEvent;
import com.study.studypal.team.event.team.UserLeftTeamEvent;
import com.study.studypal.team.exception.TeamMembershipErrorCode;
import com.study.studypal.team.repository.TeamUserRepository;
import com.study.studypal.team.service.api.TeamMembershipService;
import com.study.studypal.team.service.internal.TeamInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.team.util.TeamCursorUtils;
import com.study.studypal.user.entity.User;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamMembershipServiceImpl implements TeamMembershipService {
  private final TeamUserRepository teamUserRepository;
  private final TeamMembershipInternalService internalService;
  private final TeamInternalService teamService;
  private final TeamNotificationSettingInternalService teamNotificationSettingService;
  private final TaskInternalService taskService;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;
  private final TeamProperties props;

  /**
   * Note: Cache eviction for teamMembers is already handled inside TeamInternalService's
   * increaseMember and decreaseMember methods.
   */
  @Override
  @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#userId)")
  public ActionResponseDto joinTeam(UUID userId, String teamCode) {
    UUID teamId = teamService.getIdByTeamCode(teamCode);

    if (teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
      throw new BaseException(TeamMembershipErrorCode.USER_ALREADY_IN_TEAM);
    }

    internalService.createMembership(teamId, userId, TeamRole.MEMBER);
    teamService.increaseMember(teamId);
    teamNotificationSettingService.createSettings(userId, teamId);

    UserJoinedTeamEvent event = UserJoinedTeamEvent.builder().userId(userId).teamId(teamId).build();

    eventPublisher.publishEvent(event);

    return ActionResponseDto.builder().success(true).message("Join team successfully.").build();
  }

  @Override
  public ListTeamMemberResponseDto getTeamMembers(
      UUID userId, UUID teamId, String cursor, int size) {
    if (!teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
      throw new BaseException(TeamMembershipErrorCode.USER_MEMBERSHIP_NOT_FOUND);
    }

    // Handle cache with default condition
    Cache cache = cacheManager.getCache(CacheNames.TEAM_MEMBERS);
    boolean cacheResponse = false;
    if (cursor == null && size == 10) {
      ListTeamMemberResponseDto list =
          Objects.requireNonNull(cache)
              .get(CacheKeyUtils.of(teamId), ListTeamMemberResponseDto.class);
      if (list != null) return list;
      else cacheResponse = true;
    }

    Pageable pageable = PageRequest.of(0, size);

    List<TeamUser> memberships;
    if (cursor != null && !cursor.isEmpty()) {
      DecodedCursor decodedCursor = TeamCursorUtils.decodeCursor(cursor);
      memberships =
          teamUserRepository.findTeamMembersWithCursor(
              teamId,
              decodedCursor.getRolePriority(),
              decodedCursor.getName(),
              decodedCursor.getUserId(),
              pageable);
    } else {
      memberships = teamUserRepository.findTeamMembers(teamId, pageable);
    }

    List<TeamMemberResponseDto> members =
        memberships.stream()
            .map(
                m -> {
                  User userInfo = m.getUser();
                  return TeamMemberResponseDto.builder()
                      .userId(userInfo.getId())
                      .name(userInfo.getName())
                      .avatarUrl(userInfo.getAvatarUrl())
                      .role(m.getRole())
                      .build();
                })
            .toList();

    long total = teamUserRepository.getTotalMembers(teamId);

    String nextCursor = null;
    if (members.size() == size) {
      TeamMemberResponseDto lastMember = members.get(members.size() - 1);
      nextCursor =
          TeamCursorUtils.encodeCursor(
              lastMember.getRole().ordinal() + 1, lastMember.getName(), lastMember.getUserId());
    }

    ListTeamMemberResponseDto response =
        ListTeamMemberResponseDto.builder()
            .members(members)
            .total(total)
            .nextCursor(nextCursor)
            .build();

    if (cacheResponse) cache.put(CacheKeyUtils.of(teamId), response);

    return response;
  }

  @Override
  public ListTeamMemberResponseDto searchTeamMembersByName(
      UUID userId, UUID teamId, String keyword, UUID cursor, int size) {
    if (!teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
      throw new BaseException(TeamMembershipErrorCode.USER_MEMBERSHIP_NOT_FOUND);
    }

    String handledKeyword = keyword.toLowerCase().trim();
    Pageable pageable = PageRequest.of(0, size);

    List<TeamUser> memberships =
        teamUserRepository.searchTeamMembersWithCursor(
            userId, teamId, handledKeyword, cursor, pageable);
    List<TeamMemberResponseDto> members =
        memberships.stream()
            .map(
                m -> {
                  User userInfo = m.getUser();
                  return TeamMemberResponseDto.builder()
                      .userId(userInfo.getId())
                      .name(userInfo.getName())
                      .avatarUrl(userInfo.getAvatarUrl())
                      .role(m.getRole())
                      .build();
                })
            .toList();

    long total = teamUserRepository.countTeamMembersByName(userId, teamId, handledKeyword);
    String nextCursor =
        members.size() == size ? members.get(members.size() - 1).getUserId().toString() : null;

    return ListTeamMemberResponseDto.builder()
        .members(members)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#request.memberId)"),
        @CacheEvict(
            value = CacheNames.TEAM_DASHBOARD,
            key = "@keys.of(#request.memberId, #request.teamId)"),
        @CacheEvict(value = CacheNames.TEAM_MEMBERS, key = "@keys.of(#request.teamId)")
      })
  public ActionResponseDto updateTeamMemberRole(UUID userId, UpdateMemberRoleRequestDto request) {
    UUID teamId = request.getTeamId();
    UUID memberId = request.getMemberId();

    if (userId.equals(memberId)) {
      throw new BaseException(TeamMembershipErrorCode.CANNOT_UPDATE_OWN_ROLE);
    }

    TeamUser userInfo =
        teamUserRepository
            .findByUserIdAndTeamId(userId, teamId)
            .orElseThrow(
                () -> new BaseException(TeamMembershipErrorCode.USER_MEMBERSHIP_NOT_FOUND));

    TeamUser memberInfo =
        teamUserRepository
            .findByUserIdAndTeamId(memberId, teamId)
            .orElseThrow(
                () ->
                    new BaseException(
                        TeamMembershipErrorCode.TARGET_MEMBERSHIP_NOT_FOUND, memberId));

    if (userInfo.getRole() != TeamRole.OWNER) {
      throw new BaseException(TeamMembershipErrorCode.PERMISSION_UPDATE_MEMBER_ROLE_DENIED);
    }

    // Each group can have only one creator
    if (request.getRole() == TeamRole.OWNER) {
      if (teamService.countTeamsOwnerByUser(request.getMemberId()) == props.getMaxOwnedTeams()) {
        throw new BaseException(TeamMembershipErrorCode.TEAM_OWNER_LIMIT_REACHED);
      } else {
        userInfo.setRole(TeamRole.ADMIN);
        teamUserRepository.save(userInfo);
      }
    }

    memberInfo.setRole(request.getRole());
    teamUserRepository.save(memberInfo);

    return ActionResponseDto.builder()
        .success(true)
        .message("Update member's role successfully.")
        .build();
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#request.memberId)"),
        @CacheEvict(
            value = CacheNames.TEAM_DASHBOARD,
            key = "@keys.of(#request.memberId, #request.teamId)")
      })
  public ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request) {
    UUID memberId = request.getMemberId();
    UUID teamId = request.getTeamId();

    if (userId.equals(memberId)) {
      throw new BaseException(TeamMembershipErrorCode.CANNOT_REMOVE_SELF);
    }

    if (taskService.hasRemainingTasksInTeam(memberId, teamId)) {
      throw new BaseException(TeamMembershipErrorCode.CANNOT_REMOVE_MEMBER_WITH_REMAINING_TASKS);
    }

    // Lock user performing action
    TeamUser userInfo =
        teamUserRepository
            .findByUserIdAndTeamIdForUpdate(userId, teamId)
            .orElseThrow(
                () -> new BaseException(TeamMembershipErrorCode.USER_MEMBERSHIP_NOT_FOUND));

    // Lock member being removed
    TeamUser memberInfo =
        teamUserRepository
            .findByUserIdAndTeamIdForUpdate(memberId, teamId)
            .orElseThrow(
                () ->
                    new BaseException(
                        TeamMembershipErrorCode.TARGET_MEMBERSHIP_NOT_FOUND, memberId));

    // Permission check
    switch (userInfo.getRole()) {
      case OWNER:
        {
          break;
        }
      case ADMIN:
        {
          if (memberInfo.getRole() == TeamRole.MEMBER) {
            break;
          } else {
            throw new BaseException(TeamMembershipErrorCode.PERMISSION_REMOVE_MEMBER_RESTRICTED);
          }
        }
      case MEMBER:
        {
          throw new BaseException(TeamMembershipErrorCode.PERMISSION_REMOVE_MEMBER_RESTRICTED);
        }
    }

    // Safe delete (only one transaction can proceed at a time due to lock)
    int rowsDeleted = teamUserRepository.deleteMemberById(memberInfo.getId());
    if (rowsDeleted == 0) {
      throw new BaseException(TeamMembershipErrorCode.MEMBER_ALREADY_REMOVED);
    }

    teamService.decreaseMember(teamId);

    UserLeftTeamEvent event = UserLeftTeamEvent.builder().userId(memberId).teamId(teamId).build();

    eventPublisher.publishEvent(event);

    return ActionResponseDto.builder().success(true).message("Remove member successfully.").build();
  }

  @Override
  @Caching(
      evict = {
        @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#userId)"),
        @CacheEvict(value = CacheNames.TEAM_DASHBOARD, key = "@keys.of(#userId, #teamId)")
      })
  public ActionResponseDto leaveTeam(UUID userId, UUID teamId) {
    TeamUser membership =
        teamUserRepository
            .findByUserIdAndTeamId(userId, teamId)
            .orElseThrow(
                () -> new BaseException(TeamMembershipErrorCode.USER_MEMBERSHIP_NOT_FOUND));

    if (taskService.hasRemainingTasksInTeam(userId, teamId)) {
      throw new BaseException(TeamMembershipErrorCode.CANNOT_LEAVE_WITH_REMAINING_TASKS);
    }

    teamUserRepository.delete(membership);

    int totalMembers = teamUserRepository.getTotalMembers(teamId) - 1;

    if (totalMembers > 0 && membership.getRole() == TeamRole.OWNER) {
      throw new BaseException(TeamMembershipErrorCode.CANNOT_LEAVE_AS_CREATOR);
    }

    teamService.decreaseMember(teamId);

    UserLeftTeamEvent event = UserLeftTeamEvent.builder().userId(userId).teamId(teamId).build();

    eventPublisher.publishEvent(event);

    return ActionResponseDto.builder().success(true).message("Leave team successfully.").build();
  }
}
