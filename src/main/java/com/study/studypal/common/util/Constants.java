package com.study.studypal.common.util;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
  public static final String AUTHORIZATION_HEADER = "Authorization";
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

  public static final Set<String> IMAGE_EXTENSIONS =
      Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg");
  public static final Set<String> VIDEO_EXTENSIONS =
      Set.of("mp4", "avi", "mov", "mkv", "wmv", "flv", "webm");

  public static final String RESOURCE_TYPE_IMAGE = "image";
  public static final String RESOURCE_TYPE_VIDEO = "video";
  public static final String RESOURCE_TYPE_RAW = "raw";
  public static final List<String> VALID_RESOURCE_TYPES =
      List.of(RESOURCE_TYPE_IMAGE, RESOURCE_TYPE_VIDEO, RESOURCE_TYPE_RAW);

  public static final String UNKNOW_FILE_NAME = "unknow";
  public static final String UNKNOW_FILE_EXTENSION = "";
}
