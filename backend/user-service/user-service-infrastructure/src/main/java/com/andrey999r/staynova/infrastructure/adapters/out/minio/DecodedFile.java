package com.andrey999r.staynova.infrastructure.adapters.out.minio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

public class DecodedFile implements MultipartFile {

  private final String name;
  private final String contentType;
  private final byte[] content;

  public DecodedFile(String name, String contentType, byte[] content) {
    this.name = name;
    this.contentType = contentType;
    this.content = Objects.requireNonNull(content);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public @Nullable String getOriginalFilename() {
    return null;
  }

  @Override
  public @Nullable String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  public byte[] getBytes() {
    return content;
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(File dest) throws IOException {
    java.nio.file.Files.write(dest.toPath(), content);
  }
}
