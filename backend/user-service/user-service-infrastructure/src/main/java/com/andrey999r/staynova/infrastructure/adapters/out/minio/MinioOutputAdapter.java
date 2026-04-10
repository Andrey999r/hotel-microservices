package com.andrey999r.staynova.infrastructure.adapters.out.minio;

import com.andrey999r.staynova.domain.ports.out.ObjectStorageOutputPort;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.file.FileNotFoundException;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.file.FileUploadException;
import com.andrey999r.staynova.infrastructure.config.minio.MinioProperties;
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
public class MinioOutputAdapter implements ObjectStorageOutputPort {

  private final MinioClient minioClient;
  private final MinioProperties minioProperties;

  @PostConstruct
  public void initDefaultPhotos() {
    ensureBucketExists();
    uploadDefaultIfNotExists();
  }

  private void ensureBucketExists() {
    try {
      boolean exists =
          minioClient.bucketExists(
              BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build());
      if (!exists) {
        minioClient.makeBucket(
            MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
        log.info("Created bucket: {}", minioProperties.getBucket());
      }
    } catch (Exception e) {
      log.error("Failed to ensure bucket exists: {}", e.getMessage());
    }
  }

  private void uploadDefaultIfNotExists() {
    try {
      minioClient.statObject(
          StatObjectArgs.builder()
              .bucket(minioProperties.getBucket())
              .object("default-avatar.png")
              .build());
      log.info("Default photo already exists out MinIO: {}", "default-avatar.png");
    } catch (Exception e) {
      try {
        ClassPathResource resource = new ClassPathResource("images/default-avatar.png");
        if (!resource.exists()) {
          log.error("Default photo not found out classpath: {}", "images/default-avatar.png");
          throw new FileNotFoundException();
        }
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minioProperties.getBucket())
                .object("default-avatar.png")
                .stream(resource.getInputStream(), resource.contentLength(), -1)
                .contentType("image/png")
                .build());
        log.info("Uploaded default photo to MinIO: {}", "default-avatar.png");
      } catch (Exception ex) {
        log.error("Failed to upload default photo: {}", ex.getMessage());
      }
    }
  }

  @Override
  public String uploadPhoto(byte[] data, Login login) {
    MultipartFile file = new DecodedFile("file", "image/png", data);
    return upload(file, minioProperties.getBucket(), "avatars/" + login.login());
  }

  @Override
  public String getDefaultPicturePath() {
    return minioProperties.getPublicUrl()
        + "/"
        + minioProperties.getBucket()
        + "/default-avatar.png";
  }

  @Override
  public void delete(String path) {}

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
      throw new FileUploadException(fileName);
    }
  }

  private String getExtension(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return fileName != null && fileName.contains(".")
        ? fileName.substring(fileName.lastIndexOf("."))
        : ".jpg";
  }
}
