package com.study.studypal.chatbot.service.internal.extractor;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface TextExtractorStrategy {
  boolean support(MultipartFile file);

  String extract(MultipartFile file) throws IOException;
}
