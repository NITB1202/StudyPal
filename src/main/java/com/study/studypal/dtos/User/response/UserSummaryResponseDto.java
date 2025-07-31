package com.study.studypal.dtos.User.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryResponseDto {
    private UUID id;

    private String name;

    private String avatarUrl;
}
