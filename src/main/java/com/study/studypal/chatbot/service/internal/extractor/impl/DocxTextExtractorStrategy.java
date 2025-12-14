package com.study.studypal.chatbot.service.internal.extractor.impl;

import com.study.studypal.chatbot.service.internal.extractor.TextExtractorStrategy;
import com.study.studypal.common.util.FileUtils;
import java.io.IOException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class DocxTextExtractorStrategy implements TextExtractorStrategy {
  @Override
  public boolean support(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return fileName != null && fileName.toLowerCase().endsWith(".docx");
  }

  @Override
  public String extract(MultipartFile file) throws IOException {
    try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
      StringBuilder sb = new StringBuilder();

      for (XWPFParagraph p : doc.getParagraphs()) {
        if (isHeading(p)) {
          sb.append("\n### ").append(p.getText()).append("\n");
        } else {
          sb.append(p.getText()).append("\n");
        }
      }

      doc.getTables()
          .forEach(
              table ->
                  table
                      .getRows()
                      .forEach(
                          row -> {
                            row.getTableCells()
                                .forEach(cell -> sb.append(cell.getText()).append(" | "));
                            sb.append("\n");
                          }));

      return FileUtils.normalizeText(sb.toString());
    }
  }

  private boolean isHeading(XWPFParagraph p) {
    String style = p.getStyle();
    return style != null && style.startsWith("Heading");
  }
}
