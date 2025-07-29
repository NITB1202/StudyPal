package com.study.studypal.dtos.Auth.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateRegisterInfoRequestDto {
    @NotEmpty(message = "Username is required.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Gender gender;

    @NotEmpty(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotEmpty(message = "Password is required.")
    private String password;
}
