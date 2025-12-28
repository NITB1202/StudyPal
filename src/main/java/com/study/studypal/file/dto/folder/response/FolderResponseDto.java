package com.study.studypal.file.dto.folder.response;

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
public class FolderResponseDto {
  private UUID id;

  private String name;

  private Integer documentCount;
}
