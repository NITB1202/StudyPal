package com.study.studypal.services;

import com.study.studypal.dtos.Shared.FileResponseDto;

public interface FileService {
    FileResponseDto uploadFile(String folderPath, String publicId, byte[] bytes);
    void moveFile(String publicId, String newFolder);
    void deleteFile(String publicId);
}
