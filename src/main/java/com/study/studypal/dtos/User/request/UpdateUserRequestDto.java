package com.study.studypal.dtos.User.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.enums.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequestDto {
    @Size(min =3, max = 20, message = "Name must be between 3 and 20 characters.")
    private String name;

    @Past(message = "Date of birth must be in the past.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Gender gender;
}
