package com.study.studypal.common.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CodeErrorCode;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.CodeService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {

  @Override
  public String generateRandomCode(int codeLength) {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < codeLength; i++) {
      int rand = ThreadLocalRandom.current().nextInt(chars.length());
      sb.append(chars.charAt(rand));
    }
    return sb.toString();
  }

  @Override
  public String generateQRCodeBase64(String str, int width, int height) {
    try {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();

      EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
      hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

      BitMatrix bitMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height, hints);

      BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          int color = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
          bufferedImage.setRGB(x, y, color);
        }
      }

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", stream);
      byte[] imageBytes = stream.toByteArray();

      return Base64.getEncoder().encodeToString(imageBytes);
    } catch (WriterException | IOException e) {
      throw new BaseException(CodeErrorCode.GENERATE_QR_CODE_FAILED, e.getMessage());
    }
  }

  @Override
  public String decodeQRCode(MultipartFile file) {
    try {
      BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

      LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

      Result result = new MultiFormatReader().decode(bitmap);
      return result.getText();

    } catch (IOException ex) {
      log.error("Error reading file: {}", ex.getMessage(), ex);
      throw new BaseException(FileErrorCode.INVALID_FILE_CONTENT);

    } catch (NotFoundException ex) {
      log.error("QR code not found: {}", ex.getMessage(), ex);
      throw new BaseException(CodeErrorCode.QR_CODE_NOT_FOUND);
    }
  }
}
