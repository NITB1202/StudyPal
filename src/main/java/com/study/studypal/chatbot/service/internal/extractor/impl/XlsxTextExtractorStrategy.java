package com.study.studypal.chatbot.service.internal.extractor.impl;

import com.study.studypal.chatbot.service.internal.extractor.TextExtractorStrategy;
import com.study.studypal.common.util.FileUtils;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class XlsxTextExtractorStrategy implements TextExtractorStrategy {
  @Override
  public boolean support(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return fileName != null && fileName.toLowerCase().endsWith(".xlsx");
  }

  @Override
  public String extract(MultipartFile file) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
      StringBuilder sb = new StringBuilder();
      DataFormatter formatter = new DataFormatter();
      FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

      for (Sheet sheet : workbook) {
        sb.append("\n--- SHEET: ").append(sheet.getSheetName()).append(" ---\n");

        for (Row row : sheet) {
          for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            sb.append(formatter.formatCellValue(cell, evaluator)).append("\t");
          }
          sb.append("\n");
        }
      }

      return FileUtils.normalizeText(sb.toString());
    }
  }
}
