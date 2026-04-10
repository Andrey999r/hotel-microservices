package com.andrey999r.staynova.general.minio;

import com.andrey999r.staynova.general.exception.custom.minio.FileDeleteException;
import com.andrey999r.staynova.general.exception.custom.minio.FileUploadException;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService implements ObjectStorageService {

  private final MinioClient minioClient;
  private final MinioProperties minioProperties;

  @PostConstruct
  public void initDefaultPhotos() {
    ensureBucketExists(minioProperties.getRoomsBucket());
    ensureBucketExists(minioProperties.getHotelsBucket());
    uploadDefaultIfNotExists(
        minioProperties.getRoomsBucket(), "default-photo.png", "images/default-photo.png");
    uploadDefaultIfNotExists(
        minioProperties.getHotelsBucket(), "default-hotel.png", "images/default-hotel.png");
  }

  private void ensureBucketExists(String bucketName) {
    if (bucketName == null || bucketName.isBlank()) {
      log.warn("Bucket name is not configured, skipping");
      return;
    }
    try {
      boolean exists =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!exists) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        log.info("Created bucket: {}", bucketName);
      }
    } catch (Exception e) {
      log.error("Failed to ensure bucket exists: bucket={}, error={}", bucketName, e.getMessage());
    }
  }

  private void uploadDefaultIfNotExists(
      String bucket, String objectName, String classpathResource) {
    if (bucket == null || bucket.isBlank()) return;
    try {
      minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(objectName).build());
      log.info("Default photo already exists in MinIO: bucket={}, object={}", bucket, objectName);
    } catch (Exception e) {
      try {
        ClassPathResource resource = new ClassPathResource(classpathResource);
        if (!resource.exists()) {
          log.warn("Default photo not found in classpath: {}", classpathResource);
          return;
        }
        minioClient.putObject(
            PutObjectArgs.builder().bucket(bucket).object(objectName).stream(
                    resource.getInputStream(), resource.contentLength(), -1)
                .contentType("image/png")
                .build());
        log.info("Uploaded default photo to MinIO: bucket={}, object={}", bucket, objectName);
      } catch (Exception ex) {
        log.error(
            "Failed to upload default photo to MinIO: bucket={}, object={}, error={}",
            bucket,
            objectName,
            ex.getMessage());
      }
    }
  }

  @Override
  public String uploadRoomPhoto(MultipartFile file, Long roomId) {
    return upload(file, minioProperties.getRoomsBucket(), "photos/room-" + roomId);
  }

  @Override
  public String uploadHotelPhoto(MultipartFile file, Long hotelId) {
    return upload(file, minioProperties.getHotelsBucket(), "photos/hotel-" + hotelId);
  }

  @Override
  public void deleteObject(String fileUrl) {
    String prefix = minioProperties.getPublicUrl() + "/" + minioProperties.getRoomsBucket() + "/";
    String objectName = fileUrl.replace(prefix, "");
    log.info(
        "Deleting object from MinIO: bucket={}, object={}",
        minioProperties.getRoomsBucket(),
        objectName);
    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder()
              .bucket(minioProperties.getRoomsBucket())
              .object(objectName)
              .build());
      log.info("Object deleted from MinIO: {}", objectName);
    } catch (Exception e) {
      log.error(
          "Failed to delete object from MinIO: bucket={}, object={}, error={}",
          minioProperties.getRoomsBucket(),
          objectName,
          e.getMessage(),
          e);
      throw new FileDeleteException(fileUrl, e);
    }
  }

  @Override
  public String getDefaultPicture() {
    return minioProperties.getPublicUrl()
        + "/"
        + minioProperties.getHotelsBucket()
        + "/default-hotel.png";
  }

  private String upload(MultipartFile file, String bucket, String prefix) {
    String fileName = prefix + "-" + UUID.randomUUID() + getExtension(file);
    log.info("Uploading file to MinIO: bucket={}, object={}", bucket, fileName);
    try {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucket).object(fileName).stream(
                  file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build());
      String url = minioProperties.getPublicUrl() + "/" + bucket + "/" + fileName;
      log.info("File uploaded successfully to MinIO: {}", url);
      return url;
    } catch (Exception e) {
      log.error(
          "Failed to upload file to MinIO: bucket={}, object={}, error={}",
          bucket,
          fileName,
          e.getMessage(),
          e);
      throw new FileUploadException(fileName, e);
    }
  }

  private String getExtension(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return fileName != null && fileName.contains(".")
        ? fileName.substring(fileName.lastIndexOf("."))
        : ".bin";
  }
}
