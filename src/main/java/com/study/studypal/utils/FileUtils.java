package com.study.studypal.utils;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
    public static boolean isDocument(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        String lowerCaseName = filename.toLowerCase();
        return lowerCaseName.endsWith(".pdf") ||
                lowerCaseName.endsWith(".doc") ||
                lowerCaseName.endsWith(".docx") ||
                lowerCaseName.endsWith(".xls") ||
                lowerCaseName.endsWith(".xlsx") ||
                lowerCaseName.endsWith(".ppt") ||
                lowerCaseName.endsWith(".pptx") ||
                lowerCaseName.endsWith(".txt");
    }

    public static boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
