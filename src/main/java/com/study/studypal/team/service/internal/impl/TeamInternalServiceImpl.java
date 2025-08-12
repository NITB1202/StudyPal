package com.study.studypal.team.service.internal.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.exception.TeamErrorCode;
import com.study.studypal.team.repository.TeamRepository;
import com.study.studypal.team.service.internal.TeamInternalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamInternalServiceImpl implements TeamInternalService {
    private final TeamRepository teamRepository;

    @Override
    public UUID getTeamIdByTeamCode(String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode);

        if(team == null) {
            throw new BaseException(TeamErrorCode.INVALID_TEAM_CODE);
        }

        return team.getId();
    }

    @Override
    @CacheEvict(
            value = CacheNames.TEAM_MEMBERS,
            key = "@keys.of(#teamId)"
    )
    public void increaseMember(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND)
        );

        team.setTotalMembers(team.getTotalMembers() + 1);
        teamRepository.save(team);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = CacheNames.TEAM_MEMBERS,
            key = "@keys.of(#teamId)"
    )
    public void decreaseMember(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new BaseException(TeamErrorCode.TEAM_NOT_FOUND)
        );

        team.setTotalMembers(team.getTotalMembers() - 1);

        if(team.getTotalMembers() == 0) {
            teamRepository.delete(team);
        }
        else {
            teamRepository.save(team);
        }
    }
}
