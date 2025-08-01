package com.study.studypal.auth.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateAccessTokenResponseDto {
    private String accessToken;
}
