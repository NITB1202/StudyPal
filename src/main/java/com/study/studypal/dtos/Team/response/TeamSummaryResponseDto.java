package com.study.studypal.dtos.Team.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamSummaryResponseDto {
    private UUID id;

    private String name;

    private String avatarUrl;

    private boolean managedByUser;
}
