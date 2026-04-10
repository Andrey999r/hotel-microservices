package com.andrey999r.staynova.general.minio;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {

  String uploadRoomPhoto(MultipartFile file, Long roomId);

  String uploadHotelPhoto(MultipartFile file, Long hotelId);

  void deleteObject(String fileUrl);

  String getDefaultPicture();
}
