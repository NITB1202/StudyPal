package com.study.studypal.team.controller;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.NotFoundApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.team.dto.invitation.request.SendInvitationRequestDto;
import com.study.studypal.team.dto.invitation.response.InvitationResponseDto;
import com.study.studypal.team.dto.invitation.response.ListInvitationResponseDto;
import com.study.studypal.team.service.api.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invitations")
public class InvitationController {
  private final InvitationService invitationService;

  @PostMapping
  @Operation(summary = "Invite a user to the team.")
  @ApiResponse(responseCode = "200", description = "Invite successfully.")
  @BadRequestApiResponse
  @NotFoundApiResponse
  public ResponseEntity<InvitationResponseDto> sendInvitation(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody SendInvitationRequestDto request) {
    return ResponseEntity.ok(invitationService.sendInvitation(userId, request));
  }

  @GetMapping("/all")
  @Operation(summary = "Get a list of invitations.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<ListInvitationResponseDto> getInvitations(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(invitationService.getInvitations(userId, cursor, size));
  }

  @PostMapping("/{invitationId}")
  @Operation(summary = "Reply to the invitation.")
  @ApiResponse(responseCode = "200", description = "Reply successfully.")
  @UnauthorizedApiResponse
  @NotFoundApiResponse
  public ResponseEntity<ActionResponseDto> replyToInvitation(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID invitationId,
      @RequestParam boolean accept) {
    return ResponseEntity.ok(invitationService.replyToInvitation(invitationId, userId, accept));
  }
}
