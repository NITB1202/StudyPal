package com.study.studypal.team.service.internal;

import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TeamMembershipInternalService {
  void createMembership(UUID teamId, UUID userId, TeamRole role);

  void validateUpdateTeamPermission(UUID userId, UUID teamId);

  void validateInviteMemberPermission(UUID userId, UUID teamId, UUID inviteeId);

  TeamUser getMemberShip(UUID teamId, UUID userId);

  LocalDateTime getUserJoinedTeamsListCursor(UUID userId, UUID lastTeamId, int listSize, int size);

  List<UUID> getMemberIds(UUID teamId);

  UserSummaryProfile getOwnerProfile(UUID teamId);
}
