package com.study.studypal.notification.dto.setting.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamNotificationSettingsResponseDto {
    private UUID id;

    private boolean teamNotification;

    private boolean teamPlanReminder;

    private boolean chatNotification;
}
