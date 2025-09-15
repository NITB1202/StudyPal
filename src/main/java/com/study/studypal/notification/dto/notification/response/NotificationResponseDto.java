package com.study.studypal.notification.dto.notification.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.notification.enums.LinkedSubject;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponseDto {
  private UUID id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  private String title;

  private String content;

  private boolean isRead;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LinkedSubject subject;

  private UUID subjectId;
}
