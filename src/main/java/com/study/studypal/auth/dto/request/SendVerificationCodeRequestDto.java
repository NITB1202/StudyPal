package com.study.studypal.auth.dto.request;

import com.study.studypal.auth.enums.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
public class SendVerificationCodeRequestDto {
    @NotNull(message = "Verification type is required")
    private VerificationType type;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
