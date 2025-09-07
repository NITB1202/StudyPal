package com.study.studypal.notification.dto.notification.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnreadNotificationCountResponseDto {
    private int count;
}
