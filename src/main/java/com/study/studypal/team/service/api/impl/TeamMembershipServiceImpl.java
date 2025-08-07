package com.study.studypal.team.service.api.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.util.CacheKeyUtils;
import com.study.studypal.team.dto.TeamUser.internal.DecodedCursor;
import com.study.studypal.team.dto.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.team.dto.TeamUser.request.UpdateMemberRoleRequestDto;
import com.study.studypal.team.dto.TeamUser.response.ListTeamMemberResponseDto;
import com.study.studypal.team.dto.TeamUser.response.TeamMemberResponseDto;
import com.study.studypal.team.entity.TeamUser;
import com.study.studypal.team.service.internal.TeamInternalService;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import com.study.studypal.team.enums.TeamRole;
import com.study.studypal.common.exception.BusinessException;
import com.study.studypal.common.exception.NotFoundException;
import com.study.studypal.team.repository.TeamUserRepository;
import com.study.studypal.team.service.api.TeamMembershipService;
import com.study.studypal.team.util.CursorUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamMembershipServiceImpl implements TeamMembershipService {
    private final TeamUserRepository teamUserRepository;
    private final TeamMembershipInternalService internalService;
    private final TeamInternalService teamService;

    /**
     * Note: Cache eviction for teamMembers is already handled inside
     * TeamInternalService's increaseMember and decreaseMember methods.
     */

    @Override
    @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#userId)")
    public ActionResponseDto joinTeam(UUID userId, String teamCode) {
        UUID teamId = teamService.getTeamIdByTeamCode(teamCode);

        if(teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new BusinessException("You are already in the team.");
        }

        internalService.createMembership(teamId, userId, TeamRole.MEMBER);
        teamService.increaseMember(teamId);

        return ActionResponseDto.builder()
                .success(true)
                .message("Join team successfully.")
                .build();
    }

    @Override
    @Cacheable(
            value = CacheNames.TEAM_MEMBERS,
            key = "@keys.of(#teamId)",
            condition = "#cursor == null && #size == 10"
    )
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
    @Caching(evict = {
            @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#request.memberId)"),
            @CacheEvict(value = CacheNames.TEAM_OVERVIEW, key = "@keys.of(#request.memberId, #request.teamId)"),
            @CacheEvict(value = CacheNames.TEAM_MEMBERS, key = "@keys.of(#request.teamId)")
    })
    public ActionResponseDto updateTeamMemberRole(UUID userId, UpdateMemberRoleRequestDto request) {
        if(userId.equals(request.getMemberId())) {
            throw new BusinessException("You can't update your own role.");
        }

        TeamUser userInfo = teamUserRepository.findByUserIdAndTeamId(userId, request.getTeamId()).orElseThrow(
                ()-> new NotFoundException("User's membership not found.")
        );

        TeamUser memberInfo = teamUserRepository.findByUserIdAndTeamId(request.getMemberId(), request.getTeamId()).orElseThrow(
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
    @Caching(evict = {
            @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#request.memberId)"),
            @CacheEvict(value = CacheNames.TEAM_OVERVIEW, key = "@keys.of(#request.memberId, #request.teamId)")
    })
    public ActionResponseDto removeTeamMember(UUID userId, RemoveTeamMemberRequestDto request) {
        if(userId.equals(request.getMemberId())) {
            throw new BusinessException("You can't remove yourself from the team.");
        }

        //Lock user performing action
        TeamUser userInfo = teamUserRepository.findByUserIdAndTeamIdForUpdate(userId, request.getTeamId()).orElseThrow(
                ()-> new NotFoundException("User's membership not found.")
        );

        //Lock member being removed
        TeamUser memberInfo = teamUserRepository.findByUserIdAndTeamIdForUpdate(request.getMemberId(), request.getTeamId()).orElseThrow(
                ()-> new NotFoundException("Member's membership not found.")
        );

        //Permission check
        switch (userInfo.getRole()) {
            case CREATOR: {
                break;
            }
            case ADMIN: {
                if(memberInfo.getRole() == TeamRole.MEMBER) {
                    break;
                }
                else {
                    throw new BusinessException("Administrators can only remove members.");
                }
            }
            case MEMBER: {
                throw new BusinessException("You don't have permission to remove another member.");
            }
        }

        // Safe delete (only one transaction can proceed at a time due to lock)
        int rowsDeleted = teamUserRepository.deleteMemberById(memberInfo.getId());
        if (rowsDeleted == 0) {
            throw new BusinessException("Member has already been removed.");
        }

        teamService.decreaseMember(request.getTeamId());

        return ActionResponseDto.builder()
                .success(true)
                .message("Remove member successfully.")
                .build();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheNames.USER_TEAMS, key = "@keys.of(#userId)"),
            @CacheEvict(value = CacheNames.TEAM_OVERVIEW, key = "@keys.of(#userId, #teamId)")
    })
    public ActionResponseDto leaveTeam(UUID userId, UUID teamId) {
        TeamUser membership = teamUserRepository.findByUserIdAndTeamId(userId, teamId).orElseThrow(
                () -> new NotFoundException("Membership not found.")
        );

        teamUserRepository.delete(membership);

        int totalMembers = teamUserRepository.getTotalMembers(teamId) - 1;

        if (totalMembers > 0 && membership.getRole() == TeamRole.CREATOR) {
            throw new BusinessException("You are the creator of the team." +
                    " Please hand over your responsibilities before leaving.");
        }

        teamService.decreaseMember(teamId);

        return ActionResponseDto.builder()
                .success(true)
                .message("Leave team successfully.")
                .build();
    }
}
