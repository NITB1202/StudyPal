package com.study.studypal.common.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.study.studypal.auth.enums.VerificationType;
import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.CodeErrorCode;
import com.study.studypal.common.service.CodeService;
import com.study.studypal.common.util.CacheKeyUtils;
import jakarta.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
  private final CacheManager cacheManager;
  private Cache cache;
  private static final int LENGTH = 6;

  @PostConstruct
  public void initCaches() {
    this.cache = cacheManager.getCache(CacheNames.VERIFICATION_CODES);
  }

  @Override
  public String generateVerificationCode(String email, VerificationType type) {
    String code = generateRandomCode();
    cache.put(CacheKeyUtils.of(email), code);
    return code;
  }

  @Override
  public boolean verifyCode(String email, String code, VerificationType type) {
    String storedCode = cache.get(CacheKeyUtils.of(email), String.class);

    if (storedCode == null || !storedCode.equals(code)) {
      return false;
    }

    cache.evict(CacheKeyUtils.of(email));
    return true;
  }

  @Override
  public String generateTeamCode() {
    return generateRandomCode();
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

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", baos);
      byte[] imageBytes = baos.toByteArray();

      return Base64.getEncoder().encodeToString(imageBytes);
    } catch (WriterException | IOException e) {
      throw new BaseException(CodeErrorCode.GENERATE_QR_CODE_FAILED, e.getMessage());
    }
  }

  private String generateRandomCode() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < LENGTH; i++) {
      int rand = ThreadLocalRandom.current().nextInt(chars.length());
      sb.append(chars.charAt(rand));
    }
    return sb.toString();
  }
}
