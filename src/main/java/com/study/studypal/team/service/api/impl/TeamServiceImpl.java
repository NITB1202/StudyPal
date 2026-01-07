package com.study.studypal.team.service.api.impl;

import static com.study.studypal.common.util.Constants.RESOURCE_TYPE_IMAGE;
import static com.study.studypal.team.constant.TeamConstant.TEAM_AVATAR_FOLDER;
import static com.study.studypal.team.constant.TeamConstant.TEAM_CODE_LENGTH;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.CodeService;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.file.service.internal.FolderInternalService;
import com.study.studypal.file.service.internal.UsageService;
import com.study.studypal.notification.service.internal.TeamNotificationSettingInternalService;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.team.config.TeamProperties;
import com.study.studypal.team.dto.team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.team.response.ListTeamResponseDto;
import com.study.studypal.team.dto.team.response.TeamCodeResponseDto;
import com.study.studypal.team.dto.team.response.TeamDashboardResponseDto;
import com.study.studypal.team.dto.team.response.TeamPreviewResponseDto;
import com.study.studypal.team.dto.team.response.TeamQRCodeResponseDto;
import com.study.studypal.team.dto.team.response.TeamResponseDto;
import com.study.studypal.team.dto.team.response.TeamSummaryResponseDto;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamFilter;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.event.team.TeamDeletedEvent;
import com.study.studypal.team.event.team.TeamUpdatedEvent;
import com.study.studypal.team.exception.TeamErrorCode;
import com.study.studypal.team.exception.TeamMembershipErrorCode;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
  private final TaskCounterService taskCounterService;
  private final FolderInternalService folderService;
  private final UsageService usageService;
  private final ModelMapper modelMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final TeamProperties props;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#userId)")
  public TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request) {
    if (internalService.countTeamsOwnerByUser(userId) == props.getMaxOwnedTeams()) {
      throw new BaseException(TeamErrorCode.TEAM_OWNER_LIMIT_REACHED);
    }

    if (teamRepository.existsByNameAndCreatorId(request.getName(), userId)) {
      throw new BaseException(TeamErrorCode.DUPLICATE_TEAM_NAME);
    }

    int retry = 0;
    User creator = entityManager.getReference(User.class, userId);

    while (true) {
      String randomCode = generateTeamCode();

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

        UUID teamId = team.getId();

        teamMembershipService.createMembership(teamId, userId, TeamRole.OWNER);
        teamNotificationSettingService.createSettings(userId, teamId);
        taskCounterService.createTeamTaskCounter(teamId);
        usageService.initializeTeamUsage(teamId);
        folderService.createTeamDefaultFolder(userId, teamId);

        return modelMapper.map(team, TeamResponseDto.class);
      } catch (DataIntegrityViolationException e) {
        retry++;
        log.info("Create team retry: {}", retry);
      }
    }
  }

  @Override
  @Cacheable(value = CacheNames.TEAM_DASHBOARD, key = "@keys.of(#userId, #teamId)")
  public TeamDashboardResponseDto getTeamDashboard(UUID userId, UUID teamId) {
    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

    TeamDashboardResponseDto dashboard = modelMapper.map(team, TeamDashboardResponseDto.class);

    TeamUser membership = teamMembershipService.getMemberShip(teamId, userId);
    dashboard.setRole(membership.getRole());

    return dashboard;
  }

  @Override
  public TeamQRCodeResponseDto getTeamQRCode(UUID userId, UUID teamId, int width, int height) {
    TeamUser membership = teamMembershipService.getMemberShip(teamId, userId);

    if (membership.getRole().equals(TeamRole.MEMBER)) {
      throw new BaseException(TeamMembershipErrorCode.PERMISSION_INVITE_MEMBER_DENIED);
    }

    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));
    String qrCode = codeService.generateQRCodeBase64(team.getTeamCode(), width, height);

    return TeamQRCodeResponseDto.builder().qrCode(qrCode).build();
  }

  @Override
  public TeamCodeResponseDto decodeTeamQRCode(MultipartFile file) {
    if (!FileUtils.isImage(file)) {
      throw new BaseException(FileErrorCode.INVALID_IMAGE_FILE);
    }

    String decodedCode = codeService.decodeQRCode(file);
    if (!isValidTeamCode(decodedCode)) {
      throw new BaseException(TeamErrorCode.INVALID_TEAM_QR_CODE);
    }

    return TeamCodeResponseDto.builder().teamCode(decodedCode).build();
  }

  @Override
  public TeamPreviewResponseDto getTeamPreview(String teamCode) {
    Team team =
        teamRepository
            .findByTeamCode(teamCode)
            .orElseThrow(() -> new BaseException(TeamErrorCode.INVALID_TEAM_CODE));

    UserSummaryProfile owner = teamMembershipService.getOwnerProfile(team.getId());
    TeamPreviewResponseDto profile = modelMapper.map(team, TeamPreviewResponseDto.class);

    profile.setCreatorName(owner.getName());
    profile.setCreatorAvatarUrl(owner.getAvatarUrl());

    return profile;
  }

  @Override
  @Cacheable(
      value = CacheNames.USER_TEAMS,
      key = "@keys.of(#userId)",
      condition =
          "#keyword == null && #cursor == null && #size == 10 && #filter == T(com.study.studypal.team.enums.TeamFilter).JOINED")
  public ListTeamResponseDto searchTeamsByName(
      UUID userId, TeamFilter filter, String keyword, LocalDateTime cursor, int size) {
    String handledKeyword = keyword != null ? keyword.toLowerCase().trim() : null;
    Pageable pageable = PageRequest.of(0, size);

    List<TeamSummaryResponseDto> teams =
        switch (filter) {
          case JOINED ->
              handledKeyword != null
                  ? searchUserJoinedTeamsByName(userId, handledKeyword, cursor, pageable)
                  : getUserJoinedTeams(userId, cursor, pageable);
          case OWNED ->
              handledKeyword != null
                  ? searchUserOwnedTeamsByName(userId, handledKeyword, cursor, pageable)
                  : getUserOwnedTeams(userId, cursor, pageable);
        };

    long total =
        switch (filter) {
          case JOINED ->
              handledKeyword != null
                  ? teamRepository.countUserJoinedTeamsByName(userId, handledKeyword)
                  : teamRepository.countUserJoinedTeams(userId);
          case OWNED ->
              handledKeyword != null
                  ? teamRepository.countUserOwnedTeamsByName(userId, handledKeyword)
                  : teamRepository.countUserOwnedTeams(userId);
        };

    LocalDateTime nextCursor = null;
    if (teams.size() == size) {
      UUID lastTeamId = teams.get(teams.size() - 1).getId();
      nextCursor = teamMembershipService.getTeamListCursor(userId, lastTeamId, teams.size(), size);
    }

    return ListTeamResponseDto.builder().teams(teams).total(total).nextCursor(nextCursor).build();
  }

  private List<TeamSummaryResponseDto> searchUserJoinedTeamsByName(
      UUID userId, String handledKeyword, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? teamRepository.searchUserJoinedTeamsByName(userId, handledKeyword, pageable)
        : teamRepository.searchUserJoinedTeamsByNameWithCursor(
            userId, handledKeyword, cursor, pageable);
  }

  private List<TeamSummaryResponseDto> searchUserOwnedTeamsByName(
      UUID userId, String handledKeyword, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? teamRepository.searchUserOwnedTeamsByName(userId, handledKeyword, pageable)
        : teamRepository.searchUserOwnedTeamsByNameWithCursor(
            userId, handledKeyword, cursor, pageable);
  }

  private List<TeamSummaryResponseDto> getUserJoinedTeams(
      UUID userId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? teamRepository.findUserJoinedTeams(userId, pageable)
        : teamRepository.findUserJoinedTeamsWithCursor(userId, cursor, pageable);
  }

  private List<TeamSummaryResponseDto> getUserOwnedTeams(
      UUID userId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? teamRepository.findUserOwnedTeams(userId, pageable)
        : teamRepository.findUserOwnedTeamsWithCursor(userId, cursor, pageable);
  }

  @Override
  public TeamResponseDto updateTeam(
      UUID userId, UUID teamId, UpdateTeamRequestDto request, MultipartFile file) {
    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

    teamMembershipService.validateUpdateTeamPermission(userId, teamId);

    boolean nameChanged = false;
    boolean avatarChanged = false;

    if (request != null) {
      if (request.getName() != null) {
        if (request.getName().equals(team.getName()))
          throw new BaseException(TeamErrorCode.TEAM_NAME_UNCHANGED);

        if (teamRepository.existsByNameAndCreatorId(request.getName(), userId)) {
          throw new BaseException(TeamErrorCode.DUPLICATE_TEAM_NAME);
        }

        nameChanged = true;
      }

      modelMapper.map(request, team);
    }

    if (!ObjectUtils.isEmpty(file)) {
      String avatarUrl = uploadAvatar(teamId, file);
      team.setAvatarUrl(avatarUrl);
      avatarChanged = true;
    }

    teamRepository.save(team);

    TeamUpdatedEvent event =
        TeamUpdatedEvent.builder()
            .teamId(teamId)
            .teamName(team.getName())
            .updatedBy(userId)
            .shouldEvictCache(nameChanged || avatarChanged)
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

    String teamCode = generateTeamCode();
    while (teamRepository.existsByTeamCode(teamCode)) {
      teamCode = generateTeamCode();
    }

    team.setTeamCode(teamCode);
    teamRepository.save(team);

    return ActionResponseDto.builder().success(true).message("Reset successfully.").build();
  }

  @Override
  public ActionResponseDto deleteTeam(UUID teamId, UUID userId) {
    Team team =
        teamRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND));

    teamMembershipService.validateUpdateTeamPermission(userId, teamId);

    if (team.getAvatarUrl() != null) {
      fileService.deleteFile(team.getId().toString(), RESOURCE_TYPE_IMAGE);
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

  private String uploadAvatar(UUID teamId, MultipartFile file) {
    if (!FileUtils.isImage(file)) {
      throw new BaseException(FileErrorCode.INVALID_IMAGE_FILE);
    }

    try {
      return fileService
          .uploadFile(TEAM_AVATAR_FOLDER, teamId.toString(), file.getBytes())
          .getUrl();
    } catch (IOException e) {
      throw new BaseException(FileErrorCode.INVALID_FILE_CONTENT);
    }
  }

  private String generateTeamCode() {
    return codeService.generateRandomCode(TEAM_CODE_LENGTH);
  }

  private boolean isValidTeamCode(String teamCode) {
    if (StringUtils.isBlank(teamCode)) {
      return false;
    }

    return teamCode.length() == TEAM_CODE_LENGTH && teamCode.matches("^[A-Za-z0-9]+$");
  }
}
