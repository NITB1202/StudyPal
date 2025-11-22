package com.study.studypal.common.service;

public interface CodeService {
  String generateRandomCode(int codeLength);

  String generateQRCodeBase64(String str, int width, int height);
}
