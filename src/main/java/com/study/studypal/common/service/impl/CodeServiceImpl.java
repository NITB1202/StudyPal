package com.study.studypal.common.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CodeErrorCode;
import com.study.studypal.common.service.CodeService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
