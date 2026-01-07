package com.study.studypal.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface CodeService {
  String generateRandomCode(int codeLength);

  String generateQRCodeBase64(String str, int width, int height);

  String decodeQRCode(MultipartFile file);
}
