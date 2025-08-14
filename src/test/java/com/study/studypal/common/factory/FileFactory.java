package com.study.studypal.common.factory;

import org.springframework.mock.web.MockMultipartFile;

public class FileFactory {
    public static MockMultipartFile createRawFile() {
        return new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
    }

    public static MockMultipartFile createImageFile() {
        return new MockMultipartFile("avatar", "avatar.png", "image/png", "dummy image content".getBytes());
    }
}
