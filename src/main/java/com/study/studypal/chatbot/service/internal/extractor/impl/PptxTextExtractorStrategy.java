package com.study.studypal.chatbot.service.internal.extractor.impl;

import com.study.studypal.chatbot.service.internal.extractor.TextExtractorStrategy;
import com.study.studypal.common.util.FileUtils;
import java.io.IOException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PptxTextExtractorStrategy implements TextExtractorStrategy {
  @Override
  public boolean support(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return fileName != null && fileName.toLowerCase().endsWith(".pptx");
  }

  @Override
  public String extract(MultipartFile file) throws IOException {
    try (XMLSlideShow ppt = new XMLSlideShow(file.getInputStream())) {
      StringBuilder sb = new StringBuilder();
      int slideIndex = 1;

      for (XSLFSlide slide : ppt.getSlides()) {
        appendSlideHeader(sb, slideIndex++);
        extractSlideContent(slide, sb);
      }

      return FileUtils.normalizeText(sb.toString());
    }
  }

  private void extractSlideContent(XSLFSlide slide, StringBuilder sb) {
    for (XSLFShape shape : slide.getShapes()) {
      extractShapeContent(shape, sb);
    }
  }

  private void extractShapeContent(XSLFShape shape, StringBuilder sb) {
    if (shape instanceof XSLFTextShape textShape) {
      appendTextShape(textShape, sb);
    } else if (shape instanceof XSLFTable table) {
      appendTable(table, sb);
    }
  }

  private void appendTextShape(XSLFTextShape textShape, StringBuilder sb) {
    sb.append(textShape.getText()).append("\n");
  }

  private void appendTable(XSLFTable table, StringBuilder sb) {
    for (XSLFTableRow row : table.getRows()) {
      for (XSLFTableCell cell : row.getCells()) {
        sb.append(cell.getText()).append(" | ");
      }
      sb.append("\n");
    }
  }

  private void appendSlideHeader(StringBuilder sb, int slideIndex) {
    sb.append("\n--- SLIDE ").append(slideIndex).append(" ---\n");
  }
}
