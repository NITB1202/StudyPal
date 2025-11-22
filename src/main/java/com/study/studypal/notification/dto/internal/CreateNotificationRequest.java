package com.study.studypal.notification.dto.internal;

import com.study.studypal.notification.enums.LinkedSubject;
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
public class CreateNotificationRequest {
  private UUID userId;

  private String imageUrl;

  private String title;

  private String content;

  private LinkedSubject subject;

  private UUID subjectId;
}
