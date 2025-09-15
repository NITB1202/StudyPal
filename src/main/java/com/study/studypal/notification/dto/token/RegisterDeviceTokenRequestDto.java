package com.study.studypal.notification.dto.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.notification.enums.Platform;
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
public class RegisterDeviceTokenRequestDto {
    @NotEmpty(message = "Device token is required")
    private String deviceToken;

    @NotNull(message = "Platform is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Platform platform;
}
