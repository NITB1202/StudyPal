package com.study.studypal.common.factory;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class FileFactory {
  public static MockMultipartFile createRawFile() {
    return new MockMultipartFile(
        "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
  }

  public static MockMultipartFile createImageFile() {
    return new MockMultipartFile(
        "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "dummy image content".getBytes());
  }
}
