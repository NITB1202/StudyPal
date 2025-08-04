package com.study.studypal.team.service.internal.impl;

import com.study.studypal.common.exception.BusinessException;
import com.study.studypal.common.exception.NotFoundException;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.team.repository.TeamUserRepository;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamMembershipInternalServiceImpl implements TeamMembershipInternalService {
    private final TeamUserRepository teamUserRepository;

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
                ()->new NotFoundException("You are not a member of this team.")
        );

        if(membership.getRole() != TeamRole.CREATOR) {
            throw new BusinessException("Only creator has permission to update the team.");
        }
    }

    @Override
    public void validateInviteMemberPermission(UUID userId, UUID teamId, UUID inviteeId) {
        TeamUser membership = getMemberShip(teamId, userId);

        if(membership.getRole() == TeamRole.MEMBER) {
            throw new BusinessException("You don’t have permission to invite members to this team.");
        }

        if(teamUserRepository.existsByUserIdAndTeamId(inviteeId, teamId)) {
            throw new BusinessException("The invitee is already in the team.");
        }
    }

    @Override
    public TeamUser getMemberShip(UUID teamId, UUID userId) {
        return teamUserRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                ()->new NotFoundException("You are not a member of this team.")
        );
    }

    @Override
    public LocalDateTime getUserJoinedTeamsListCursor(UUID userId, UUID lastTeamId, int listSize, int size) {
        TeamUser membership = getMemberShip(lastTeamId, userId);
        return listSize > 0 && listSize == size ? membership.getJoinedAt() : null;
    }
}
