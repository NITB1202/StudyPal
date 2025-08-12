package com.study.studypal.auth.dto.request;

import com.study.studypal.auth.enums.ExternalAuthProvider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginWithProviderRequestDto {
    @NotNull(message = "Provider is required")
    private ExternalAuthProvider provider;

    @NotEmpty(message = "Access token is required")
    private String accessToken;
}
