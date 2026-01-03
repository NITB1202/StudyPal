package com.study.studypal.chat.dto.message.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class MarkMessagesAsReadRequestDto {
  @NotNull(message = "Ids are required.")
  @Size(min = 1, message = "The list must contain at least 1 item.")
  private List<UUID> messageIds;
}
