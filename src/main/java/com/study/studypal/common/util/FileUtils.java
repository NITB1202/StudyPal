package com.study.studypal.common.util;

import static com.study.studypal.common.util.Constants.DOCUMENT_CONTENT_TYPES;
import static com.study.studypal.common.util.Constants.DOCUMENT_EXTENSIONS;

import java.text.Normalizer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

  public static boolean isDocument(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return false;
    }

    String filename = file.getOriginalFilename();
    String contentType = file.getContentType();

    if (filename == null || contentType == null) {
      return false;
    }

    String lowerName = filename.toLowerCase();

    boolean validExtension = DOCUMENT_EXTENSIONS.stream().anyMatch(lowerName::endsWith);
    boolean validContentType = DOCUMENT_CONTENT_TYPES.contains(contentType);

    return validExtension && validContentType;
  }

  public static boolean isImage(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null && contentType.startsWith("image/");
  }

  public static String normalizeText(String text) {
    if (StringUtils.isBlank(text)) return "";

    // Unicode normalization
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFC);

    // Remove invisible/control characters (keep newline, tab)
    normalized = normalized.replaceAll("[\\p{Cc}\\p{Cf}&&[^\n\t]]", "");

    // Normalize line endings
    normalized = normalized.replaceAll("\\r\\n?", "\n");

    // Normalize whitespace
    normalized = normalized.replaceAll("[ \\t]+", " ").replaceAll("\\n{3,}", "\n\n");

    return normalized.trim();
  }

  public static String fixBrokenPdfLines(String text) {
    return text.replaceAll("(?<![.?!:;])\\n(?!\\n|\\s*(?:[-•*]|\\d+\\.))", " ");
  }
}
