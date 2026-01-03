package com.study.studypal.chatbot.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatMessageAttachmentErrorCode implements ErrorCode {
  ATTACHMENT_UNSUPPORTED_TYPE(
      HttpStatus.BAD_REQUEST,
      "CHATBOT_ATTACH_001",
      "Only text-based documents (pdf, docx, xlsx, pptx, txt) are allowed for AI processing."),
  ATTACHMENT_SIZE_EXCEEDED(
      HttpStatus.BAD_REQUEST, "CHATBOT_ATTACH_002", "Attachment size exceeds the allowed limit."),
  ATTACHMENT_TOTAL_SIZE_EXCEEDED(
      HttpStatus.BAD_REQUEST,
      "CHATBOT_ATTACH_003",
      "Total attachment size exceeds the allowed limit."),
  ATTACHMENT_EXTRACTION_FAILED(
      HttpStatus.BAD_REQUEST, "CHATBOT_ATTACH_004", "Failed to extract document content: %s"),
  ;
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ChatMessageAttachmentErrorCode(
      final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
