package com.study.studypal.plan.dto.plan.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPlanRequestDto {
  @NotBlank(message = "Key word is required")
  private String keyword;

  @NotNull(message = "From date is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime fromDate;

  @NotNull(message = "To date is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime toDate;

  private String cursor;

  @NotNull(message = "Size is required")
  @Positive(message = "Size must be positive")
  private Integer size;
}
