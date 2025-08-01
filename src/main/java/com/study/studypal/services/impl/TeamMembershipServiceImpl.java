package com.study.studypal.services.impl;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.TeamUser.internal.DecodedCursor;
import com.study.studypal.dtos.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.dtos.TeamUser.request.UpdateMemberRoleRequestDto;
import com.study.studypal.dtos.TeamUser.response.ListTeamMemberResponseDto;
import com.study.studypal.dtos.TeamUser.response.TeamMemberResponseDto;
import com.study.studypal.dtos.TeamUser.response.UserRoleInTeamResponseDto;
import com.study.studypal.entities.Team;
import com.study.studypal.entities.TeamUser;
import com.study.studypal.entities.TeamUserId;
import com.study.studypal.entities.User;
import com.study.studypal.enums.TeamRole;
import com.study.studypal.exceptions.BusinessException;
import com.study.studypal.exceptions.NotFoundException;
import com.study.studypal.repositories.TeamRepository;
import com.study.studypal.repositories.TeamUserRepository;
import com.study.studypal.services.TeamMembershipService;
import com.study.studypal.utils.CursorUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamMembershipServiceImpl implements TeamMembershipService {
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public ActionResponseDto joinTeam(UUID userId, String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode);

        if(team == null) {
            throw new NotFoundException("Team code is incorrect.");
        }

        if(teamUserRepository.existsByUserIdAndTeamId(userId, team.getId())) {
            throw new BusinessException("You are already in the team.");
        }

        User user = entityManager.getReference(User.class, userId);

        TeamUser membership = TeamUser.builder()
                .id(new TeamUserId(team.getId(), userId))
                .team(team)
                .user(user)
                .role(TeamRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        teamUserRepository.save(membership);

        team.setTotalMembers(team.getTotalMembers() + 1);
        teamRepository.save(team);

        return ActionResponseDto.builder()
                .success(true)
                .message("Join team successfully.")
                .build();
    }

    @Override
    public UserRoleInTeamResponseDto getUserRoleInTeam(UUID userId, UUID teamId) {
        TeamUser membership = teamUserRepository.findById(new TeamUserId(teamId, userId)).orElseThrow(
                ()-> new NotFoundException("You are not in this team.")
        );

        return UserRoleInTeamResponseDto.builder()
                .userId(userId)
                .role(membership.getRole())
                .build();
    }

    @Override
    public ListTeamMemberResponseDto getTeamMembers(UUID teamId, String cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<TeamUser> memberships;
        if (cursor != null && !cursor.isEmpty()) {
            DecodedCursor decodedCursor = CursorUtils.decodeCursor(cursor);
            memberships = teamUserRepository.findTeamMembersWithCursor(
                    teamId,
                    decodedCursor.getRolePriority(),
                    decodedCursor.getName(),
                    decodedCursor.getUserId(),
                    pageable
            );
        }
        else {
            memberships = teamUserRepository.findTeamMembers(teamId, pageable);
        }

        List<TeamMemberResponseDto> members = memberships.stream()
                .map(m -> {
                        User userInfo = m.getUser();
                        return TeamMemberResponseDto.builder()
                                .userId(userInfo.getId())
                                .name(userInfo.getName())
                                .avatarUrl(userInfo.getAvatarUrl())
                                .role(m.getRole())
                                .build();
                    }
                )
                .toList();

        long total = teamUserRepository.getTotalMembers(teamId);

        String nextCursor = null;
        if(!members.isEmpty() && members.size() == size) {
            TeamMemberResponseDto lastMember = members.get(members.size() - 1);
            nextCursor = CursorUtils.encodeCursor(lastMember.getRole().ordinal() + 1, lastMember.getName(), lastMember.getUserId());
        }

        return ListTeamMemberResponseDto.builder()
                .members(members)
                .total(total)
                .nextCursor(nextCursor)
                .build();
    }

    @Override
    public ListTeamMemberResponseDto searchTeamMembersByName(UUID userId, UUID teamId, String keyword, UUID cursor, int size) {
        String handledKeyword = keyword.toLowerCase().trim();
        Pageable pageable = PageRequest.of(0, size);

        List<TeamUser> memberships = teamUserRepository.searchTeamMembersWithCursor(userId, teamId, handledKeyword, cursor, pageable);
        List<TeamMemberResponseDto> members = memberships.stream()
                .map(m -> {
                            User userInfo = m.getUser();
                            return TeamMemberResponseDto.builder()
                                    .userId(userInfo.getId())
                                    .name(userInfo.getName())
                                    .avatarUrl(userInfo.getAvatarUrl())
                                    .role(m.getRole())
                                    .build();
                        }
                )
                .toList();

        long total = teamUserRepository.countTeamMembersByName(userId, teamId, handledKeyword);
        String nextCursor = !members.isEmpty() && members.size() == size ? members.get(members.size() - 1).getUserId().toString() : null;

        return ListTeamMemberResponseDto.builder()
                .members(members)
                .total(total)
                .nextCursor(nextCursor)
                .build();
    }

    @Override
    public ActionResponseDto updateTeamMemberRole(UUID userId, UpdateMemberRoleRequestDto request) {
        if(userId.equals(request.getMemberId())) {
            throw new BusinessException("You can't update your own role.");
        }

        TeamUser userInfo = teamUserRepository.findById(new TeamUserId(request.getTeamId(), userId)).orElseThrow(
                ()-> new NotFoundException("User's membership not found.")
        );

        TeamUser memberInfo = teamUserRepository.findById(new TeamUserId(request.getTeamId(), request.getMemberId())).orElseThrow(
                ()-> new NotFoundException("Member's membership not found.")
        );

        if(userInfo.getRole() != TeamRole.CREATOR) {
            throw new BusinessException("Only the creator can update another member's role.");
        }

        //Each group can have only one creator
        if(request.getRole() == TeamRole.CREATOR) {
            userInfo.setRole(TeamRole.ADMIN);
            teamUserRepository.save(userInfo);
        }

        memberInfo.setRole(request.getRole());
        teamUserRepository.save(memberInfo);

        return ActionResponseDto.builder()
                .success(true)
                .message("Update member's role successfully.")
                .build();
    }

    @Override
    @Transactional
    public ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request) {
        if(userId.equals(request.getMemberId())) {
            throw new BusinessException("You can't remove yourself from the team.");
        }

        TeamUser userInfo = teamUserRepository.findById(new TeamUserId(request.getTeamId(), userId)).orElseThrow(
                ()-> new NotFoundException("User's membership not found.")
        );

        TeamUser memberInfo = teamUserRepository.findById(new TeamUserId(request.getTeamId(), request.getMemberId())).orElseThrow(
                ()-> new NotFoundException("Member's membership not found.")
        );

        switch (userInfo.getRole()) {
            case CREATOR: {
                removeTeamMember(memberInfo);
                break;
            }
            case ADMIN: {
                if(memberInfo.getRole() == TeamRole.MEMBER) {
                    removeTeamMember(memberInfo);
                }
                else {
                    throw new BusinessException("Administrators can only remove members.");
                }
                break;
            }
            case MEMBER: {
                throw new BusinessException("You don't have permission to remove another member.");
            }
        }

        return ActionResponseDto.builder()
                .success(true)
                .message("Remove member successfully.")
                .build();
    }

    private void removeTeamMember(TeamUser membership) {
        teamUserRepository.delete(membership);
        Team team = membership.getTeam();
        team.setTotalMembers(team.getTotalMembers() - 1);
        teamRepository.save(team);
    }

    @Override
    @Transactional
    public ActionResponseDto leaveTeam(UUID userId, UUID teamId) {
        TeamUser membership = teamUserRepository.findById(new TeamUserId(teamId, userId)).orElseThrow(
                ()-> new NotFoundException("Membership not found.")
        );

        teamUserRepository.delete(membership);

        Team team = membership.getTeam();
        team.setTotalMembers(team.getTotalMembers() - 1);

        if(team.getTotalMembers() == 0) {
            teamRepository.delete(team);
        }
        else {
            if (membership.getRole() == TeamRole.CREATOR) {
                throw new BusinessException("You are the creator of the team." +
                        " Please hand over your responsibilities before leaving.");
            }

            teamRepository.save(team);
        }

        return ActionResponseDto.builder()
                .success(true)
                .message("Leave team successfully.")
                .build();
    }
}
