package com.study.studypal.notification.dto.setting.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamNotificationSettingResponseDto {
  private UUID id;

  private boolean teamNotification;

  private boolean teamPlanReminder;

  private boolean chatNotification;
}
