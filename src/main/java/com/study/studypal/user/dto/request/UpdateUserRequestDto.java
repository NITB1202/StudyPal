package com.study.studypal.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.user.enums.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
public class UpdateUserRequestDto {
  @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
  private String name;

  @Past(message = "Date of birth must be in the past")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dateOfBirth;

  private Gender gender;
}
