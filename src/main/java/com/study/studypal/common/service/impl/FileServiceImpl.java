package com.study.studypal.common.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.study.studypal.common.dto.FileResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.errorCode.FileErrorCode;
import com.study.studypal.common.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final Cloudinary cloudinary;
    private final static List<String> validResourceTypes = List.of("image", "video", "raw");

    @Override
    public FileResponseDto uploadFile(String folderPath, String publicId, byte[] bytes) {
        Map params = ObjectUtils.asMap(
                "resource_type", "auto",
                "public_id", publicId,
                "asset_folder", folderPath,
                "overwrite", "true"
        );

        try {
            Map result = cloudinary.uploader().upload(bytes, params);
            String url = result.get("secure_url").toString();
            long fileSizeInBytes = ((Number) result.get("bytes")).longValue();

            return FileResponseDto.builder()
                    .url(url)
                    .bytes(fileSizeInBytes)
                    .build();
        }
        catch (IOException e) {
            throw new BaseException(FileErrorCode.UPLOAD_FILE_FAILED);
        }
    }

    @Override
    public void moveFile(String publicId, String newFolder) {
        try {
            Map params =  ObjectUtils.asMap(
                    "asset_folder", newFolder,
                    "resource_type", "raw"
            );

            cloudinary.api().update(publicId, params);
        } catch (Exception e) {
            throw new BaseException(FileErrorCode.MOVING_FILE_FAILED);
        }
    }

    @Override
    public void deleteFile(String publicId, String resourceType) {
        if(!validResourceTypes.contains(resourceType)){
            throw new BaseException(FileErrorCode.INVALID_RESOURCE_TYPE);
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
        }
        catch (IOException e) {
            throw new BaseException(FileErrorCode.DELETE_FILE_FAILED);
        }
    }
}