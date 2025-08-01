package com.study.studypal.team.dto.Team.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTeamRequestDto {
    @Size(min =3, max = 20, message = "Name must be between 3 and 20 characters.")
    private String name;

    private String description;
}
