package com.study.studypal.auth.dto.request;

import com.study.studypal.auth.enums.ExternalAuthProvider;
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
