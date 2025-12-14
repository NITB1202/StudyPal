package com.study.studypal.common.util;

import java.text.Normalizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
  private FileUtils() {}

  public static boolean isDocument(MultipartFile file) {
    String filename = file.getOriginalFilename();
    if (filename == null) {
      return false;
    }

    String lowerCaseName = filename.toLowerCase();
    return lowerCaseName.endsWith(".pdf")
        || lowerCaseName.endsWith(".doc")
        || lowerCaseName.endsWith(".docx")
        || lowerCaseName.endsWith(".xls")
        || lowerCaseName.endsWith(".xlsx")
        || lowerCaseName.endsWith(".ppt")
        || lowerCaseName.endsWith(".pptx")
        || lowerCaseName.endsWith(".txt");
  }

  public static boolean isImage(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null && contentType.startsWith("image/");
  }

  public static String normalizeText(String text) {
    if (StringUtils.isBlank(text)) return "";

    // 1. Unicode normalization
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFC);

    // 2. Remove invisible/control characters (keep newline, tab)
    normalized = normalized.replaceAll("[\\p{Cc}\\p{Cf}&&[^\n\t]]", "");

    // 3. Normalize line endings
    normalized = normalized.replaceAll("\\r\\n?", "\n");

    // 4. Normalize whitespace
    normalized = normalized.replaceAll("[ \\t]+", " ").replaceAll("\\n{3,}", "\n\n");

    return normalized.trim();
  }

  public static String fixBrokenPdfLines(String text) {
    return text.replaceAll("(?<![.?!:;])\\n(?!\\n)", " ");
  }
}
