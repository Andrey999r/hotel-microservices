package com.andrey999r.staynova.hotel.bl;

import com.andrey999r.staynova.hotel.bl.dto.CreateHotelDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.bl.dto.UpdateHotelDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface HotelAdminService {

  HotelDto createHotel(CreateHotelDto dto);

  HotelDto updateHotel(Long hotelId, UpdateHotelDto dto);

  void deleteHotel(Long hotelId);

  void uploadMainPhoto(Long hotelId, MultipartFile file);

  void uploadGalleryPhotos(Long hotelId, List<MultipartFile> files);

  void deleteGalleryPhoto(Long hotelId, int photoIndex);
}
