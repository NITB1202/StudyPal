package com.study.studypal.chatbot.service.internal.extractor.impl;

import com.study.studypal.chatbot.service.internal.extractor.TextExtractorStrategy;
import com.study.studypal.common.util.FileUtils;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdfTextExtractorStrategy implements TextExtractorStrategy {
  @Override
  public boolean support(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return fileName != null && fileName.toLowerCase().endsWith(".pdf");
  }

  @Override
  public String extract(MultipartFile file) throws IOException {
    try (PDDocument document = PDDocument.load(file.getInputStream())) {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setSortByPosition(true);

      String normalizedText = FileUtils.normalizeText(stripper.getText(document));
      return FileUtils.fixBrokenPdfLines(normalizedText);
    }
  }
}
