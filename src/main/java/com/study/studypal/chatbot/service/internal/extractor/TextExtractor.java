package com.study.studypal.chatbot.service.internal.extractor;

import com.study.studypal.chatbot.exception.ChatMessageAttachmentErrorCode;
import com.study.studypal.common.exception.BaseException;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class TextExtractor {
  private final List<TextExtractorStrategy> extractors;

  public String extract(MultipartFile file) {
    return extractors.stream()
        .filter(extractor -> extractor.support(file))
        .findFirst()
        .map(
            extractor -> {
              try {
                return extractor.extract(file);
              } catch (IOException e) {
                throw new RuntimeException("Failed to extract document content", e);
              }
            })
        .orElseThrow(
            () -> new BaseException(ChatMessageAttachmentErrorCode.ATTACHMENT_UNSUPPORTED_TYPE));
  }
}
