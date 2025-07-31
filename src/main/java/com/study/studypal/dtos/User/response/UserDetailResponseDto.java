package com.study.studypal.dtos.User.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailResponseDto {
    private UUID id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Gender gender;

    private String avatarUrl;
}
