package com.study.studypal.dtos.Auth.request;

import com.study.studypal.enums.ExternalAuthProvider;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginWithProviderRequestDto {
    @NotNull(message = "Provider is required.")
    private ExternalAuthProvider provider;

    @NotNull(message = "Access token is required.")
    private String accessToken;
}
