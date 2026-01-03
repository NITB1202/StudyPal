package com.study.studypal.chat.controller;

import static com.study.studypal.common.util.Constants.DEFAULT_PAGE_SIZE;

import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.response.ListMessageResponseDto;
import com.study.studypal.chat.service.api.ChatService;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}")
public class ChatController {
  private final ChatService chatService;

  @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Send a message to a team chat")
  @ApiResponse(responseCode = "200", description = "Send successfully.")
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  public ResponseEntity<ActionResponseDto> sendMessage(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @RequestPart(value = "request") @Valid SendMessageRequestDto request,
      @RequestPart(value = "files", required = false) List<MultipartFile> attachments) {
    return ResponseEntity.ok(chatService.sendMessage(userId, teamId, request, attachments));
  }

  @GetMapping("/messages")
  @Operation(summary = "Get a list of messages.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ListMessageResponseDto> getMessages(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID teamId,
      @RequestParam(required = false) LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
    return ResponseEntity.ok(chatService.getMessages(userId, teamId, cursor, size));
  }
}
