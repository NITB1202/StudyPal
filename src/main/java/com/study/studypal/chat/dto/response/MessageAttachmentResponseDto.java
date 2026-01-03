package com.study.studypal.chat.dto.response;

import com.study.studypal.chat.enums.FileType;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageAttachmentResponseDto {
  private UUID id;

  private String name;

  private String url;

  private FileType type;
}
