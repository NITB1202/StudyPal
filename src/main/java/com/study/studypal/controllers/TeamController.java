package com.study.studypal.controllers;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.Team.request.CreateTeamRequestDto;
import com.study.studypal.dtos.Team.request.UpdateTeamRequestDto;
import com.study.studypal.dtos.Team.response.ListTeamResponseDto;
import com.study.studypal.dtos.Team.response.TeamOverviewResponseDto;
import com.study.studypal.dtos.Team.response.TeamProfileResponseDto;
import com.study.studypal.dtos.Team.response.TeamResponseDto;
import com.study.studypal.exceptions.ErrorResponse;
import com.study.studypal.services.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a new team.")
    @ApiResponse(responseCode = "200", description = "Create successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TeamResponseDto> createTeam(@AuthenticationPrincipal UUID userId,
                                                      @Valid @RequestBody CreateTeamRequestDto request){
        return ResponseEntity.ok(teamService.createTeam(userId, request));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "Get team overview.")
    @ApiResponse(responseCode = "200", description = "Get successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TeamOverviewResponseDto> getTeamOverview(@AuthenticationPrincipal UUID userId,
                                                                   @PathVariable UUID teamId){
        return ResponseEntity.ok(teamService.getTeamOverview(userId, teamId));
    }

    @GetMapping("/code/{teamCode}")
    @Operation(summary = "Get team's profile by team code.")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TeamProfileResponseDto> getTeamProfileByTeamCode(@PathVariable String teamCode){
        return ResponseEntity.ok(teamService.getTeamProfileByTeamCode(teamCode));
    }

    @GetMapping("/all")
    @Operation(summary = "Get part of the user's teams.")
    @ApiResponse(responseCode = "200", description = "Get successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ListTeamResponseDto> getUserJoinedTeams(@AuthenticationPrincipal UUID userId,
                                                                  @RequestParam(required = false) LocalDateTime cursor,
                                                                  @RequestParam(defaultValue = "10") @Positive int size){
        return ResponseEntity.ok(teamService.getUserJoinedTeams(userId, cursor, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Search for user's teams by name.")
    @ApiResponse(responseCode = "200", description = "Search successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ListTeamResponseDto> searchUserJoinedTeamsByName(@AuthenticationPrincipal UUID userId,
                                                                           @RequestParam String keyword,
                                                                           @RequestParam(required = false) LocalDateTime cursor,
                                                                           @RequestParam(defaultValue = "10") @Positive int size ){
        return ResponseEntity.ok(teamService.searchUserJoinedTeamsByName(userId, keyword, cursor, size));
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "Update team's profile.")
    @ApiResponse(responseCode = "200", description = "Update successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TeamResponseDto> updateTeam(@AuthenticationPrincipal UUID userId,
                                                      @PathVariable UUID teamId,
                                                      @Valid @RequestBody UpdateTeamRequestDto request){
        return ResponseEntity.ok(teamService.updateTeam(userId, teamId, request));
    }

    @PatchMapping("/reset/{teamId}")
    @Operation(summary = "Reset team code.")
    @ApiResponse(responseCode = "200", description = "Reset successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> resetTeamCode(@AuthenticationPrincipal UUID userId,
                                                           @PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.resetTeamCode(userId, teamId));
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "Delete a team.")
    @ApiResponse(responseCode = "200", description = "Delete successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> deleteTeam(@PathVariable UUID teamId,
                                                        @AuthenticationPrincipal UUID userId){
        return ResponseEntity.ok(teamService.deleteTeam(teamId, userId));
    }

    @PostMapping(value = "/avatar/{teamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload team's avatar.")
    @ApiResponse(responseCode = "200", description = "Upload successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request body.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> uploadTeamAvatar(@AuthenticationPrincipal UUID userId,
                                                              @PathVariable UUID teamId,
                                                              @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(teamService.uploadTeamAvatar(userId, teamId, file));
    }
}
