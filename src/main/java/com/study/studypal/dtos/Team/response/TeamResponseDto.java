package com.study.studypal.dtos.Team.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamResponseDto {
    private UUID id;

    private String name;
    
    private String description;
}
