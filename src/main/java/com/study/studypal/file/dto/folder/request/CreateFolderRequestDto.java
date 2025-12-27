package com.study.studypal.file.dto.folder.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CreateFolderRequestDto {
  @NotBlank(message = "Name is required")
  @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
  private String name;
}
