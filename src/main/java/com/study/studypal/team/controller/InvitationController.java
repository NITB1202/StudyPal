package com.study.studypal.team.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.ErrorResponse;
import com.study.studypal.team.dto.Invitation.request.SendInvitationRequestDto;
import com.study.studypal.team.dto.Invitation.response.InvitationResponseDto;
import com.study.studypal.team.dto.Invitation.response.ListInvitationResponseDto;
import com.study.studypal.team.service.api.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invitations")
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping
    @Operation(summary = "Invite a user to the team.")
    @ApiResponse(responseCode = "200", description = "Invite successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<InvitationResponseDto> sendInvitation(@AuthenticationPrincipal UUID userId,
                                                                @Valid @RequestBody SendInvitationRequestDto request){
        return ResponseEntity.ok(invitationService.sendInvitation(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get a list of invitations.")
    @ApiResponse(responseCode = "200", description = "Get successfully.")
    public ResponseEntity<ListInvitationResponseDto> getInvitations(@AuthenticationPrincipal UUID userId,
                                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(invitationService.getInvitations(userId, cursor, size));
    }

    @PostMapping("/{invitationId}")
    @Operation(summary = "Reply to the invitation.")
    @ApiResponse(responseCode = "200", description = "Reply successfully.")
    @ApiResponse(responseCode = "404", description = "Not found.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ActionResponseDto> replyToInvitation(@PathVariable UUID invitationId,
                                                               @AuthenticationPrincipal UUID userId,
                                                               @RequestParam boolean accept) {
        return ResponseEntity.ok(invitationService.replyToInvitation(invitationId, userId, accept));
    }
}
