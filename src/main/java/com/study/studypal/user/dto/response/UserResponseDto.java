package com.study.studypal.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.user.enums.Gender;
import java.time.LocalDate;
import java.util.UUID;
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
public class UserResponseDto {
  private UUID id;

  private String name;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateOfBirth;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Gender gender;
}
