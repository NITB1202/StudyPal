package com.study.studypal.dtos.User.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListUserResponseDto {
    private List<UserSummaryResponseDto> users;

    private long total;

    private UUID nextCursor;
}
