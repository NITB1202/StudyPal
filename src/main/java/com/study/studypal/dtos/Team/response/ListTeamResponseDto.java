package com.study.studypal.dtos.Team.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListTeamResponseDto {
    private List<TeamSummaryResponseDto> teams;

    private Long total;

    private String nextCursor;
}
