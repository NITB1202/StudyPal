package com.study.studypal.auth.dto.request;

import com.study.studypal.auth.enums.ExternalAuthProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginWithProviderRequestDto {
  @NotNull(message = "Provider is required")
  private ExternalAuthProvider provider;

  @NotBlank(message = "Access token is required")
  private String accessToken;
}
