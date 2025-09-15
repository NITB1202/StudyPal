package com.study.studypal.notification.dto.setting.request;

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
public class UpdateTeamNotificationSettingRequestDto {
  private Boolean teamNotification;

  private Boolean teamPlanReminder;

  private Boolean chatNotification;
}
