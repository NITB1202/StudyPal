package com.study.studypal.common.service;

import com.study.studypal.common.dto.FileResponseDto;

public interface FileService {
    FileResponseDto uploadFile(String folderPath, String publicId, byte[] bytes);
    void moveFile(String publicId, String newFolder);
    void deleteFile(String publicId, String resourceType);
}
