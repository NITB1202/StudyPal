package com.study.studypal.notification.controller;

import static com.study.studypal.common.util.Constants.DEFAULT_PAGE_SIZE;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.annotation.UnauthorizedApiResponse;
import com.study.studypal.notification.dto.notification.request.DeleteNotificationsRequestDto;
import com.study.studypal.notification.dto.notification.request.MarkNotificationsAsReadRequestDto;
import com.study.studypal.notification.dto.notification.response.ListNotificationResponseDto;
import com.study.studypal.notification.dto.notification.response.UnreadNotificationCountResponseDto;
import com.study.studypal.notification.service.api.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService notificationService;

  @GetMapping("/all")
  @Operation(summary = "Get a list of notifications.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<ListNotificationResponseDto> getNotifications(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) LocalDateTime cursor,
      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
    return ResponseEntity.ok(notificationService.getNotifications(userId, cursor, size));
  }

  @GetMapping("/unread/count")
  @Operation(summary = "Returns the total number of unread notifications for the current user.")
  @ApiResponse(responseCode = "200", description = "Get successfully.")
  public ResponseEntity<UnreadNotificationCountResponseDto> getUnreadNotificationCount(
      @AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(notificationService.getUnreadNotificationCount(userId));
  }

  @PatchMapping
  @Operation(summary = "Mark the selected notifications as read.")
  @ApiResponse(responseCode = "200", description = "Mark successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ActionResponseDto> markNotificationsAsRead(
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody MarkNotificationsAsReadRequestDto request) {
    return ResponseEntity.ok(notificationService.markNotificationsAsRead(userId, request));
  }

  @PatchMapping("/all")
  @Operation(summary = "Mark all notifications as read.")
  @ApiResponse(responseCode = "200", description = "Mark successfully.")
  public ResponseEntity<ActionResponseDto> markAllNotificationsAsRead(
      @AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(notificationService.markAllNotificationsAsRead(userId));
  }

  @DeleteMapping
  @Operation(summary = "Delete the selected notifications.")
  @ApiResponse(responseCode = "200", description = "Delete successfully.")
  @UnauthorizedApiResponse
  public ResponseEntity<ActionResponseDto> deleteNotifications(
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody DeleteNotificationsRequestDto request) {
    return ResponseEntity.ok(notificationService.deleteNotifications(userId, request));
  }

  @DeleteMapping("/all")
  @Operation(summary = "Delete all notifications.")
  @ApiResponse(responseCode = "200", description = "Delete successfully.")
  public ResponseEntity<ActionResponseDto> deleteAllNotifications(
      @AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(notificationService.deleteAllNotifications(userId));
  }
}
