package com.andrey999r.staynova.hotel.bl.api.admin;

import com.andrey999r.staynova.hotel.bl.HotelAdminService;
import com.andrey999r.staynova.hotel.bl.dto.CreateHotelDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.bl.dto.UpdateHotelDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/hotels")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class HotelAdminController {

  public static final String BY_ID = "/{id}";
  public static final String MAIN_PHOTO = "/{id}/main";
  public static final String GALLERY = "/{id}/gallery";
  public static final String DELETE_PHOTO = "/{id}/photo/{photoIndex}";

  private final HotelAdminService hotelAdminService;

  @PostMapping
  public ResponseEntity<HotelDto> create(@RequestBody @Valid CreateHotelDto dto) {
    log.info("POST /admin/hotels name='{}'", dto.name());
    HotelDto created = hotelAdminService.createHotel(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping(BY_ID)
  public ResponseEntity<HotelDto> update(
      @PathVariable("id") Long hotelId, @RequestBody @Valid UpdateHotelDto dto) {
    log.info("PUT /admin/hotels/{}", hotelId);
    return ResponseEntity.ok(hotelAdminService.updateHotel(hotelId, dto));
  }

  @DeleteMapping(BY_ID)
  public ResponseEntity<Void> delete(@PathVariable("id") Long hotelId) {
    log.info("DELETE /admin/hotels/{}", hotelId);
    hotelAdminService.deleteHotel(hotelId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = MAIN_PHOTO, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadMainPhoto(
      @PathVariable("id") Long hotelId, @RequestParam("file") MultipartFile file) {
    log.info("POST /admin/hotels/{}/main file='{}'", hotelId, file.getOriginalFilename());
    hotelAdminService.uploadMainPhoto(hotelId, file);
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = GALLERY, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadGallery(
      @PathVariable("id") Long hotelId, @RequestParam("files") List<MultipartFile> files) {
    log.info("POST /admin/hotels/{}/gallery count={}", hotelId, files.size());
    hotelAdminService.uploadGalleryPhotos(hotelId, files);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(DELETE_PHOTO)
  public ResponseEntity<Void> deletePhoto(
      @PathVariable("id") Long hotelId, @PathVariable("photoIndex") int photoIndex) {
    log.info("DELETE /admin/hotels/{}/photo/{}", hotelId, photoIndex);
    hotelAdminService.deleteGalleryPhoto(hotelId, photoIndex);
    return ResponseEntity.noContent().build();
  }
}
