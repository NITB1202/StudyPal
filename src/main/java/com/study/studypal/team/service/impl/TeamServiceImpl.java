package com.study.studypal.team.service.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.dto.Team.request.CreateTeamRequestDto;
import com.study.studypal.team.dto.Team.request.UpdateTeamRequestDto;
import com.study.studypal.team.dto.Team.response.*;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.TeamInternalService;
import com.study.studypal.user.entity.User;
import com.study.studypal.common.exception.BusinessException;
import com.study.studypal.common.exception.NotFoundException;
import com.study.studypal.team.repository.TeamRepository;
import com.study.studypal.common.service.CodeService;
import com.study.studypal.common.service.FileService;
import com.study.studypal.team.service.TeamService;
import com.study.studypal.common.util.FileUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService, TeamInternalService {
    private final TeamRepository teamRepository;
    private final CodeService codeService;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private final EntityManager entityManager;
    private static final String AVATAR_FOLDER = "teams";

    @Override
    @Transactional
    public TeamResponseDto createTeam(UUID userId, CreateTeamRequestDto request) {
        if(teamRepository.existsByNameAndCreatorId(request.getName(), userId)){
            throw new BusinessException("You have already created a team with the same name.");
        }

        int retry = 0;
        User creator = entityManager.getReference(User.class, userId);

        while(true){
            String randomCode = codeService.generateTeamCode();

            try{
                Team team = Team.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .teamCode(randomCode)
                        .createdAt(LocalDateTime.now())
                        .creator(creator)
                        .totalMembers(1)
                        .build();

                teamRepository.save(team);

                return modelMapper.map(team, TeamResponseDto.class);
            }
            catch (DataIntegrityViolationException e){
                retry++;
                System.out.println("Retry: " + retry);
            }
        }
    }

    @Override
    public TeamOverviewResponseDto getTeamOverview(UUID userId, UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                ()->new NotFoundException("Team not found.")
        );

        return modelMapper.map(team, TeamOverviewResponseDto.class);
    }

    @Override
    public TeamProfileResponseDto getTeamProfileByTeamCode(String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode);

        if(team == null){
            throw new NotFoundException("Team not found.");
        }

        User creator = team.getCreator();
        TeamProfileResponseDto profile = modelMapper.map(team, TeamProfileResponseDto.class);

        profile.setCreatorName(creator.getName());
        profile.setCreatorAvatarUrl(creator.getAvatarUrl());

        return profile;
    }

    @Override
    public ListTeamResponseDto getUserJoinedTeams(UUID userId, LocalDateTime cursor, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<Team> teams = teamRepository.findUserJoinedTeamWithCursor(userId, cursor, pageable);
        List<TeamSummaryResponseDto> summaries = modelMapper.map(teams, new TypeToken<List<TeamSummaryResponseDto>>() {}.getType());

        long total = teamRepository.countUserJoinedTeam(userId);

        return ListTeamResponseDto.builder()
                .teams(summaries)
                .total(total)
                .build();
    }

    @Override
    public ListTeamResponseDto searchUserJoinedTeamsByName(UUID userId, String keyword, LocalDateTime cursor, int size) {
        String handledKeyword = keyword.toLowerCase().trim();
        Pageable pageable = PageRequest.of(0, size);

        List<Team> teams = teamRepository.searchUserJoinedTeamByNameWithCursor(userId, handledKeyword, cursor, pageable);
        List<TeamSummaryResponseDto> summaries = modelMapper.map(teams, new TypeToken<List<TeamSummaryResponseDto>>() {}.getType());

        long total = teamRepository.countUserJoinedTeamByName(userId, handledKeyword);

        return ListTeamResponseDto.builder()
                .teams(summaries)
                .total(total)
                .build();
    }

    @Override
    public TeamResponseDto updateTeam(UUID userId, UUID teamId, UpdateTeamRequestDto request) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new NotFoundException("Team not found.")
        );

        if(request.getName() != null) {
            if(request.getName().isEmpty()) {
                throw new BusinessException("Name cannot be empty.");
            }

            if(request.getName().equals(team.getName()))
                throw new BusinessException("The new name is the same as the old one.");

            if(teamRepository.existsByNameAndCreatorId(request.getName(), userId)){
                throw new BusinessException("You have already created a team with the same name.");
            }
        }

        modelMapper.map(request, team);
        teamRepository.save(team);

        return modelMapper.map(team, TeamResponseDto.class);
    }

    @Override
    public ActionResponseDto resetTeamCode(UUID userId, UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new NotFoundException("Team not found.")
        );

        String teamCode = codeService.generateTeamCode();
        while(teamRepository.existsByTeamCode(teamCode)){
            teamCode = codeService.generateTeamCode();
        }

        team.setTeamCode(teamCode);
        teamRepository.save(team);

        return ActionResponseDto.builder()
                .success(true)
                .message(teamCode)
                .build();
    }

    @Override
    @Transactional
    public ActionResponseDto deleteTeam(UUID teamId, UUID userId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                ()-> new NotFoundException("Team not found.")
        );

        fileService.deleteFile(team.getId().toString(), "image");
        teamRepository.delete(team);

        return ActionResponseDto.builder()
                .success(true)
                .message("The team has been deleted.")
                .build();
    }

    @Override
    public ActionResponseDto uploadTeamAvatar(UUID userId, UUID teamId, MultipartFile file) {
        if(!FileUtils.isImage(file)) {
            throw new BusinessException("Team's avatar must be an image.");
        }

        try {
            String avatarUrl = fileService.uploadFile(AVATAR_FOLDER, teamId.toString(), file.getBytes()).getUrl();
            Team team = teamRepository.findById(teamId).orElseThrow(
                    () -> new NotFoundException("Team not found.")
            );

            team.setAvatarUrl(avatarUrl);
            teamRepository.save(team);

            return ActionResponseDto.builder()
                    .success(true)
                    .message("Uploaded avatar successfully.")
                    .build();

        } catch (IOException e) {
            throw new BusinessException("Reading file failed.");
        }
    }



    @Override
    public UUID getTeamIdByTeamCode(String teamCode) {
        Team team = teamRepository.findByTeamCode(teamCode);

        if(team == null) {
            throw new NotFoundException("Team code is incorrect.");
        }

        return team.getId();
    }

    @Override
    public void increaseMember(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new NotFoundException("Team not found.")
        );

        team.setTotalMembers(team.getTotalMembers() + 1);
        teamRepository.save(team);
    }

    @Override
    @Transactional
    public void decreaseMember(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new NotFoundException("Team not found.")
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
