package com.study.studypal.team.dto.TeamUser.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListTeamMemberResponseDto {
    private List<TeamMemberResponseDto> members;

    private long total;

    private String nextCursor;
}
