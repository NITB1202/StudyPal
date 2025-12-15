package com.study.studypal.chatbot.service.internal.extractor.impl;

import com.study.studypal.chatbot.service.internal.extractor.TextExtractorStrategy;
import com.study.studypal.common.util.FileUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class TxtTextExtractorStrategy implements TextExtractorStrategy {
  @Override
  public boolean support(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return fileName != null && fileName.toLowerCase().endsWith(".txt");
  }

  @Override
  public String extract(MultipartFile file) throws IOException {
    String text = new String(file.getBytes(), StandardCharsets.UTF_8);
    return FileUtils.normalizeText(text);
  }
}
