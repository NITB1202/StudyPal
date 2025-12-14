package com.study.studypal.common.util;

import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  public static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";
  public static final String DEFAULT_PAGE_SIZE = "10";
  public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
  public static final DateTimeFormatter DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern(DATE_PATTERN);
  public static final Set<String> DOCUMENT_EXTENSIONS =
      Set.of(".txt", ".pdf", ".docx", ".xlsx", ".pptx");
  public static final Set<String> DOCUMENT_CONTENT_TYPES =
      Set.of(
          "text/plain",
          "application/pdf",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          "application/vnd.openxmlformats-officedocument.presentationml.presentation");
}
