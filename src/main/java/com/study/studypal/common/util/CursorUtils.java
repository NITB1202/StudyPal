package com.study.studypal.common.util;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CommonErrorCode;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CursorUtils {
  private static final String SEPARATOR = "|";

  /** Generic cursor encoder Example: encode("2025-01-01T10:00", "2", "adam") */
  public static String encode(Object... parts) {
    if (parts == null || parts.length == 0) {
      throw new BaseException(CommonErrorCode.CURSOR_ENCODE_FAILED);
    }

    String raw =
        Stream.of(parts).map(String::valueOf).reduce((a, b) -> a + SEPARATOR + b).orElse("");
    return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  /** Decode cursor to list of string parts */
  public static List<String> decode(String encodedCursor) {
    try {
      String decoded =
          new String(Base64.getDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
      return List.of(decoded.split("\\|", -1));
    } catch (Exception e) {
      throw new BaseException(CommonErrorCode.CURSOR_DECODE_FAILED, e.getMessage());
    }
  }
}
