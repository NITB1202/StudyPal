package com.study.studypal.chat.dto.message.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
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
public class MessageResponseDto {
  private UUID id;

  private UUID userId;

  private String name;

  private String avatarUrl;

  private String content;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDateTime updatedAt;

  private String imageUrl;

  private List<String> readBy;

  private Boolean isDeleted;
}
