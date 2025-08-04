package com.study.studypal.notification.dto.TeamNotificationSettings.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTeamNotificationSettingsRequestDto {
    private Boolean teamNotification;

    private Boolean teamPlanReminder;

    private Boolean chatNotification;
}
