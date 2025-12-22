package com.study.studypal.chatbot.dto.internal;

import com.study.studypal.chatbot.dto.response.ChatResponseDto;
import com.study.studypal.chatbot.enums.IdempotencyStatus;
import com.study.studypal.chatbot.exception.ChatIdempotencyErrorCode;
import com.study.studypal.common.exception.BaseException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatIdempotencyResult {
  private IdempotencyStatus status;
  private ChatResponseDto response;

  // ===== Factory methods =====

  public static ChatIdempotencyResult acquired() {
    return new ChatIdempotencyResult(IdempotencyStatus.ACQUIRED, null);
  }

  public static ChatIdempotencyResult processing() {
    return new ChatIdempotencyResult(IdempotencyStatus.PROCESSING, null);
  }

  public static ChatIdempotencyResult done(ChatResponseDto response) {
    return new ChatIdempotencyResult(IdempotencyStatus.DONE, response);
  }

  public static ChatIdempotencyResult failed() {
    return new ChatIdempotencyResult(IdempotencyStatus.FAILED, null);
  }

  // ===== Helper methods =====

  public boolean isProcessing() {
    return status == IdempotencyStatus.PROCESSING;
  }

  public boolean isDone() {
    return status == IdempotencyStatus.DONE;
  }

  public ChatResponseDto getResponse() {
    if (!isDone()) {
      throw new BaseException(ChatIdempotencyErrorCode.CHAT_IDEMPOTENCY_RESPONSE_NOT_AVAILABLE);
    }

    return response;
  }
}
