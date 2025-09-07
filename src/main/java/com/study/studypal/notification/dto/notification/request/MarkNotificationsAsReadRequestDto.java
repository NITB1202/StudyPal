package com.study.studypal.notification.dto.notification.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarkNotificationsAsReadRequestDto {
    @NotNull(message = "Ids are required.")
    @Size(min = 1, message = "The list can't be empty.")
    private List<UUID> ids;
}
