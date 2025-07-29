package com.study.studypal.dtos.Auth.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private String accessToken;

    private String refreshToken;
}
