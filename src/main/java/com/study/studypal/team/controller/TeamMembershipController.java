package com.study.studypal.team.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.coordinator.TeamMembershipCoordinator;
import com.study.studypal.team.dto.TeamUser.request.RemoveTeamMemberRequestDto;
import com.study.studypal.team.dto.TeamUser.request.UpdateMemberRoleRequestDto;
import com.study.studypal.team.dto.TeamUser.response.ListTeamMemberResponseDto;
import com.study.studypal.team.dto.TeamUser.response.UserRoleInTeamResponseDto;
import com.study.studypal.common.exception.ErrorResponse;
import com.study.studypal.team.service.TeamMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/members")
public class TeamMembershipController {
    private final TeamMembershipService teamMembershipService;
    private final TeamMembershipCoordinator teamMembershipCoordinator;

    @PostMapping
    @Operation(summary = "Join a team by team code.")
    @ApiResponse(responseCode = "200", description = "Join successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> joinTeam(@AuthenticationPrincipal UUID userId,
                                                      @RequestParam String teamCode){
        return ResponseEntity.ok(teamMembershipCoordinator.joinTeam(userId, teamCode));
    }

    @GetMapping
    @Operation(summary = "Get the user's role in a team.")
    @ApiResponse(responseCode = "200", description = "Get successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UserRoleInTeamResponseDto> getUserRoleInTeam(@AuthenticationPrincipal UUID userId,
                                                                       @RequestParam UUID teamId){
        return ResponseEntity.ok(teamMembershipService.getUserRoleInTeam(userId, teamId));
    }

    @GetMapping("/all")
    @Operation(summary = "Get a list of team members.")
    @ApiResponse(responseCode = "200", description = "Get successfully.")
    public ResponseEntity<ListTeamMemberResponseDto> getTeamMembers(@RequestParam UUID teamId,
                                                                    @RequestParam(required = false) String cursor,
                                                                    @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(teamMembershipService.getTeamMembers(teamId, cursor, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for members by name.")
    @ApiResponse(responseCode = "200", description = "Search successfully.")
    public ResponseEntity<ListTeamMemberResponseDto> searchTeamMembersByUsername(@AuthenticationPrincipal UUID userId,
                                                                                 @RequestParam UUID teamId,
                                                                                 @RequestParam String keyword,
                                                                                 @RequestParam(required = false) UUID cursor,
                                                                                 @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(teamMembershipService.searchTeamMembersByName(userId, teamId, keyword, cursor, size));
    }

    @PatchMapping
    @Operation(summary = "Update the role of a specific team member.")
    @ApiResponse(responseCode = "200", description = "Update successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> updateTeamMemberRole(@AuthenticationPrincipal UUID userId,
                                                                  @Valid @RequestBody UpdateMemberRoleRequestDto request){
        return ResponseEntity.ok(teamMembershipService.updateTeamMemberRole(userId, request));
    }

    @DeleteMapping
    @Operation(summary = "Remove a member from the team.")
    @ApiResponse(responseCode = "200", description = "Delete successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> removeTeamMember(@AuthenticationPrincipal UUID userId,
                                                              @Valid @RequestBody RemoveTeamMemberRequestDto request){
        return ResponseEntity.ok(teamMembershipCoordinator.removeTeamMember(userId, request));
    }

    @DeleteMapping("/leave")
    @Operation(summary = "Leave a team.")
    @ApiResponse(responseCode = "200", description = "Leave successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> leaveTeam(@AuthenticationPrincipal UUID userId,
                                                       @RequestParam UUID teamId){
        return ResponseEntity.ok(teamMembershipCoordinator.leaveTeam(userId, teamId));
    }
}
