package com.study.studypal.chatbot.controller;

import static com.study.studypal.common.util.Constants.DEFAULT_PAGE_SIZE;

import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.dto.response.ListChatMessageResponseDto;
import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import com.study.studypal.chatbot.service.api.ChatBotService;
import com.study.studypal.common.exception.annotation.BadRequestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatbotController {
  private final ChatBotService chatBotService;

  @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Send a message to the chatbot.")
  @ApiResponse(responseCode = "200", description = "Send successfully.")
  @BadRequestApiResponse
  public ResponseEntity<ChatResponseDto> sendMessage(
      @AuthenticationPrincipal UUID userId,
      @RequestPart("request") @Valid ChatRequestDto request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    return ResponseEntity.ok(chatBotService.sendMessage(userId, request, files));
  }

  @GetMapping("/messages")
  @Operation(summary = "Get a list of messages.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<ListChatMessageResponseDto> getMessages(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
    return ResponseEntity.ok(chatBotService.getMessages(userId, cursor, size));
  }

  @GetMapping("/usage")
  @Operation(summary = "Get user quota usage.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<UserQuotaUsageResponseDto> getUsage(@AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(chatBotService.getUsage(userId));
  }
}
