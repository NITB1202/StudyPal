package com.study.studypal.team.service.api.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.CodeService;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.notification.service.internal.TeamNotificationSettingInternalService;
import com.study.studypal.team.dto.team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.team.response.ListTeamResponseDto;
import com.study.studypal.team.dto.team.response.TeamOverviewResponseDto;
import com.study.studypal.team.dto.team.response.TeamProfileResponseDto;
import com.study.studypal.team.dto.team.response.TeamResponseDto;
import com.study.studypal.team.dto.team.response.TeamSummaryResponseDto;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.event.team.TeamCodeResetEvent;
import com.study.studypal.team.event.team.TeamDeletedEvent;
import com.study.studypal.team.event.team.TeamUpdatedEvent;
import com.study.studypal.team.exception.TeamErrorCode;
import com.study.studypal.team.repository.TeamRepository;
import com.study.studypal.team.service.api.TeamService;
import com.study.studypal.team.service.internal.TeamInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
  private final TeamRepository teamRepository;
  private final TeamInternalService internalService;
  private final TeamMembershipInternalService teamMembershipService;
  private final TeamNotificationSettingInternalService teamNotificationSettingService;
  private final CodeService codeService;
  private final FileService fileService;
  private final ModelMapper modelMapper;
  private final ApplicationEventPublisher eventPublisher;

  @PersistenceContext private final EntityManager entityManager;
  private static final String AVATAR_FOLDER = "teams";
  private static final int MAX_OWNED_TEAMS = 5;

  @Override
  @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#userId)")
  public TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request) {
    if (internalService.countTeamsOwnerByUser(userId) == MAX_OWNED_TEAMS) {
      throw new BaseException(TeamErrorCode.TEAM_OWNER_LIMIT_REACHED);
    }

    if (teamRepository.existsByNameAndCreatorId(request.getName(), userId)) {
      throw new BaseException(TeamErrorCode.DUPLICATE_TEAM_NAME);
    }

    int retry = 0;
    User creator = entityManager.getReference(User.class, userId);

    while (true) {
      String randomCode = codeService.generateTeamCode();

      try {
        Team team =
            Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .teamCode(randomCode)
                .createdAt(LocalDateTime.now())
                .creator(creator)
                .totalMembers(1)
                .build();

        teamRepository.save(team);
        teamMembershipService.createMembership(team.getId(), userId, TeamRole.OWNER);
        teamNotificationSettingService.createSettings(userId, team.getId());

        return modelMapper.map(team, TeamResponseDto.class);
      } catch (DataIntegrityViolationException e) {
        retry++;
        log.info("Create team retry: {}", retry);
      }
    }
  }

  @Override
  @Cacheable(value = CacheNames.TEAM_OVERVIEW, key = "@keys.of(#userId, #teamId)")
  public TeamOverviewResponseDto getTeamOverview(UUID userId, UUID teamId) {
    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

    TeamOverviewResponseDto overview = modelMapper.map(team, TeamOverviewResponseDto.class);

    TeamUser membership = teamMembershipService.getMemberShip(teamId, userId);
    overview.setRole(membership.getRole());
    if (membership.getRole() == TeamRole.MEMBER) {
      overview.setTeamCode(null);
    }

    return overview;
  }

  @Override
  public TeamProfileResponseDto getTeamProfileByTeamCode(String teamCode) {
    Team team = teamRepository.findByTeamCode(teamCode);

    if (team == null) {
      throw new BaseException(TeamErrorCode.INVALID_TEAM_CODE);
    }

    UserSummaryProfile owner = teamMembershipService.getOwnerProfile(team.getId());
    TeamProfileResponseDto profile = modelMapper.map(team, TeamProfileResponseDto.class);

    profile.setCreatorName(owner.getName());
    profile.setCreatorAvatarUrl(owner.getAvatarUrl());

    return profile;
  }

  @Override
  @Cacheable(
      value = CacheNames.USER_TEAMS,
      key = "@keys.of(#userId)",
      condition = "#cursor == null && #size == 10")
  public ListTeamResponseDto getUserJoinedTeams(UUID userId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    List<TeamSummaryResponseDto> teams =
        cursor == null
            ? teamRepository.findUserJoinedTeam(userId, pageable)
            : teamRepository.findUserJoinedTeamWithCursor(userId, cursor, pageable);

    long total = teamRepository.countUserJoinedTeam(userId);

    LocalDateTime nextCursor = null;
    if (!teams.isEmpty()) {
      UUID lastTeamId = teams.get(teams.size() - 1).getId();
      nextCursor =
          teamMembershipService.getUserJoinedTeamsListCursor(
              userId, lastTeamId, teams.size(), size);
    }

    return ListTeamResponseDto.builder().teams(teams).total(total).nextCursor(nextCursor).build();
  }

  @Override
  public ListTeamResponseDto searchUserJoinedTeamsByName(
      UUID userId, String keyword, LocalDateTime cursor, int size) {
    String handledKeyword = keyword.toLowerCase().trim();
    Pageable pageable = PageRequest.of(0, size);

    List<TeamSummaryResponseDto> teams =
        cursor == null
            ? teamRepository.searchUserJoinedTeamByName(userId, handledKeyword, pageable)
            : teamRepository.searchUserJoinedTeamByNameWithCursor(
                userId, handledKeyword, cursor, pageable);

    long total = teamRepository.countUserJoinedTeamByName(userId, handledKeyword);

    LocalDateTime nextCursor = null;
    if (!teams.isEmpty()) {
      UUID lastTeamId = teams.get(teams.size() - 1).getId();
      nextCursor =
          teamMembershipService.getUserJoinedTeamsListCursor(
              userId, lastTeamId, teams.size(), size);
    }

    return ListTeamResponseDto.builder().teams(teams).total(total).nextCursor(nextCursor).build();
  }

  @Override
  public TeamResponseDto updateTeam(UUID userId, UUID teamId, UpdateTeamRequestDto request) {
    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

    teamMembershipService.validateUpdateTeamPermission(userId, teamId);

    if (request.getName() != null) {
      if (request.getName().equals(team.getName()))
        throw new BaseException(TeamErrorCode.TEAM_NAME_UNCHANGED);

      if (teamRepository.existsByNameAndCreatorId(request.getName(), userId)) {
        throw new BaseException(TeamErrorCode.DUPLICATE_TEAM_NAME);
      }
    }

    modelMapper.map(request, team);
    teamRepository.save(team);

    TeamUpdatedEvent event =
        TeamUpdatedEvent.builder()
            .teamId(teamId)
            .teamName(team.getName())
            .updatedBy(userId)
            .memberIds(teamMembershipService.getMemberIds(teamId))
            .shouldEvictCache(request.getName() != null)
            .build();

    eventPublisher.publishEvent(event);

    return modelMapper.map(team, TeamResponseDto.class);
  }

  @Override
  public ActionResponseDto resetTeamCode(UUID userId, UUID teamId) {
    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

    teamMembershipService.validateUpdateTeamPermission(userId, teamId);

    String teamCode = codeService.generateTeamCode();
    while (teamRepository.existsByTeamCode(teamCode)) {
      teamCode = codeService.generateTeamCode();
    }

    team.setTeamCode(teamCode);
    teamRepository.save(team);

    TeamCodeResetEvent event =
        TeamCodeResetEvent.builder()
            .teamId(teamId)
            .memberIds(teamMembershipService.getMemberIds(teamId))
            .build();

    eventPublisher.publishEvent(event);

    return ActionResponseDto.builder().success(true).message(teamCode).build();
  }

  @Override
  public ActionResponseDto deleteTeam(UUID teamId, UUID userId) {
    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

    teamMembershipService.validateUpdateTeamPermission(userId, teamId);

    if (team.getAvatarUrl() != null) {
      fileService.deleteFile(team.getId().toString(), "image");
    }

    TeamDeletedEvent event =
        TeamDeletedEvent.builder()
            .teamId(teamId)
            .teamName(team.getName())
            .deletedBy(userId)
            .memberIds(teamMembershipService.getMemberIds(teamId))
            .build();

    teamRepository.delete(team);
    eventPublisher.publishEvent(event);

    return ActionResponseDto.builder().success(true).message("The team has been deleted.").build();
  }

  @Override
  public ActionResponseDto uploadTeamAvatar(UUID userId, UUID teamId, MultipartFile file) {
    if (!FileUtils.isImage(file)) {
      throw new BaseException(FileErrorCode.INVALID_IMAGE_FILE);
    }

    teamMembershipService.validateUpdateTeamPermission(userId, teamId);

    try {
      String avatarUrl =
          fileService.uploadFile(AVATAR_FOLDER, teamId.toString(), file.getBytes()).getUrl();
      Team team =
          teamRepository
              .findById(teamId)
              .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

      team.setAvatarUrl(avatarUrl);
      teamRepository.save(team);

      TeamUpdatedEvent event =
          TeamUpdatedEvent.builder()
              .teamId(teamId)
              .teamName(team.getName())
              .updatedBy(userId)
              .memberIds(teamMembershipService.getMemberIds(teamId))
              .shouldEvictCache(true)
              .build();

      eventPublisher.publishEvent(event);

      return ActionResponseDto.builder()
          .success(true)
          .message("Uploaded avatar successfully.")
          .build();

    } catch (IOException e) {
      throw new BaseException(FileErrorCode.INVALID_FILE_CONTENT);
    }
  }
}
