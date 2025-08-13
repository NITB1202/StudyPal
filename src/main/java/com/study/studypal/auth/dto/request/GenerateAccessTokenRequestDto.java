package com.study.studypal.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateAccessTokenRequestDto {
    @NotEmpty(message = "Refresh token is required")
    private String refreshToken;
}
