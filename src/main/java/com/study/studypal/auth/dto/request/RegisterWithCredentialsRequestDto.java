package com.study.studypal.auth.dto.request;

import jakarta.validation.constraints.*;
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
public class RegisterWithCredentialsRequestDto {
  @NotEmpty(message = "Name is required")
  @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
  private String name;

  @NotEmpty(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotEmpty(message = "Password is required")
  private String password;
}
