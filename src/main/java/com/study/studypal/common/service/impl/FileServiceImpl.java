package com.study.studypal.common.service.impl;

import static com.study.studypal.common.util.Constants.RESOURCE_TYPE_IMAGE;
import static com.study.studypal.common.util.Constants.RESOURCE_TYPE_RAW;
import static com.study.studypal.common.util.Constants.RESOURCE_TYPE_VIDEO;
import static com.study.studypal.common.util.Constants.VALID_RESOURCE_TYPES;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.study.studypal.common.dto.FileResponse;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
  private final Cloudinary cloudinary;

  @Override
  public FileResponse uploadFile(String folderPath, String publicId, byte[] bytes) {
    Map params =
        ObjectUtils.asMap(
            "resource_type",
            "auto",
            "public_id",
            publicId,
            "asset_folder",
            folderPath,
            "overwrite",
            "true");

    try {
      Map result = cloudinary.uploader().upload(bytes, params);
      String url = result.get("secure_url").toString();
      long fileSizeInBytes = ((Number) result.get("bytes")).longValue();

      return FileResponse.builder().url(url).bytes(fileSizeInBytes).build();
    } catch (IOException e) {
      throw new BaseException(FileErrorCode.UPLOAD_FILE_FAILED);
    }
  }

  @Override
  public void moveFile(String publicId, String newFolderPath, String resourceType) {
    try {
      Map params = ObjectUtils.asMap("asset_folder", newFolderPath, "resource_type", resourceType);

      cloudinary.api().update(publicId, params);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new BaseException(FileErrorCode.MOVING_FILE_FAILED);
    }
  }

  @Override
  public void deleteFile(String publicId, String resourceType) {
    if (!VALID_RESOURCE_TYPES.contains(resourceType)) {
      throw new BaseException(FileErrorCode.INVALID_RESOURCE_TYPE);
    }

    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
    } catch (IOException e) {
      throw new BaseException(FileErrorCode.DELETE_FILE_FAILED);
    }
  }

  @Override
  public String getResourceType(String fileExtension) {
    if (FileUtils.isImage(fileExtension) || fileExtension.equals("pdf")) {
      return RESOURCE_TYPE_IMAGE;
    }

    if (FileUtils.isVideo(fileExtension)) {
      return RESOURCE_TYPE_VIDEO;
    }

    return RESOURCE_TYPE_RAW;
  }
}
