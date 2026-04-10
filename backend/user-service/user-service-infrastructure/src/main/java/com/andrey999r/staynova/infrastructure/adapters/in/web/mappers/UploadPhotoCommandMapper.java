package com.andrey999r.staynova.infrastructure.adapters.in.web.mappers;

import com.andrey999r.staynova.domain.ports.in.commands.UploadPhotoCommand;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.file.FileUploadException;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public final class UploadPhotoCommandMapper {

  private UploadPhotoCommandMapper() {}

  public static UploadPhotoCommand toCommand(MultipartFile file) {
    try {
      return new UploadPhotoCommand(file.getBytes(), file.getOriginalFilename());
    } catch (IOException e) {
      throw new FileUploadException(file.getOriginalFilename());
    }
  }
}
