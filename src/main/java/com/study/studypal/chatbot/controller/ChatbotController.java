package com.study.studypal.chatbot.controller;

import static com.study.studypal.common.util.Constants.DEFAULT_PAGE_SIZE;
import static com.study.studypal.common.util.Constants.IDEMPOTENCY_KEY_HEADER;

import com.study.studypal.chatbot.dto.request.ChatRequestDto;
import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.dto.response.ListChatMessageResponseDto;
import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import com.study.studypal.chatbot.service.api.ChatbotService;
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
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatbotController {
  private final ChatbotService chatbotService;

  @PostMapping(
      value = "/messages",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @Operation(summary = "Send a message to the chatbot and receive streamed response.")
  @ApiResponse(responseCode = "200", description = "Send successfully.")
  @BadRequestApiResponse
  public Flux<ServerSentEvent<ChatResponseDto>> sendMessage(
      @AuthenticationPrincipal UUID userId,
      @RequestHeader(IDEMPOTENCY_KEY_HEADER) String idempotencyKey,
      @RequestPart("request") @Valid ChatRequestDto request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    return chatbotService.sendMessage(userId, request, files, idempotencyKey);
  }

  @GetMapping("/messages")
  @Operation(summary = "Get a list of messages.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<ListChatMessageResponseDto> getMessages(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
    return ResponseEntity.ok(chatbotService.getMessages(userId, cursor, size));
  }

  @GetMapping("/usage")
  @Operation(summary = "Get user quota usage.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<UserQuotaUsageResponseDto> getUsage(@AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(chatbotService.getUsage(userId));
  }
}
