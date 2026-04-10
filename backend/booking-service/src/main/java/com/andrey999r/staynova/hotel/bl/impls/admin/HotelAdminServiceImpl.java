package com.andrey999r.staynova.hotel.bl.impls.admin;

import com.andrey999r.staynova.general.exception.custom.staynova.HotelHasActiveRoomsException;
import com.andrey999r.staynova.general.exception.custom.staynova.HotelNotFoundException;
import com.andrey999r.staynova.general.exception.custom.staynova.InvalidHotelIdException;
import com.andrey999r.staynova.general.exception.custom.room.EmptyFileException;
import com.andrey999r.staynova.general.exception.custom.room.InvalidPhotoIdException;
import com.andrey999r.staynova.general.minio.ObjectStorageService;
import com.andrey999r.staynova.hotel.bl.HotelAdminService;
import com.andrey999r.staynova.hotel.bl.HotelPopularityService;
import com.andrey999r.staynova.hotel.bl.HotelSearchService;
import com.andrey999r.staynova.hotel.bl.dto.CreateHotelDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.bl.dto.UpdateHotelDto;
import com.andrey999r.staynova.hotel.dal.HotelRatingRepository;
import com.andrey999r.staynova.hotel.dal.HotelRepository;
import com.andrey999r.staynova.hotel.dal.entities.HotelEntity;
import com.andrey999r.staynova.reservations.dal.ReservationStatus;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelAdminServiceImpl implements HotelAdminService {

  private final HotelRepository hotelRepository;
  private final HotelRatingRepository hotelRatingRepository;
  private final HotelSearchService hotelSearchService;
  private final HotelPopularityService hotelPopularityService;
  private final ObjectStorageService minioService;

  @Override
  @Transactional
  public HotelDto createHotel(CreateHotelDto dto) {
    log.info("Creating hotel: name='{}'", dto.name());
    HotelEntity hotel = new HotelEntity();
    hotel.setName(dto.name());
    hotel.setDescription(dto.description());
    hotel.setAddress(dto.address());
    hotel.setCountry(dto.country());
    hotel.setMainPhotoUrl(minioService.getDefaultPicture());
    HotelEntity saved = hotelRepository.save(hotel);
    hotelSearchService.index(saved);
    hotelPopularityService.init(saved.getId());
    log.info("Hotel created: id={}, name='{}'", saved.getId(), saved.getName());
    return toDto(saved, false);
  }

  @Override
  @Transactional
  public HotelDto updateHotel(Long hotelId, UpdateHotelDto dto) {
    validateId(hotelId);
    log.info("Updating hotel id={}", hotelId);
    HotelEntity hotel =
        hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(hotelId));

    if (dto.name() != null && !dto.name().isBlank()) hotel.setName(dto.name());
    if (dto.description() != null) hotel.setDescription(dto.description());
    if (dto.address() != null) hotel.setAddress(dto.address());
    if (dto.country() != null) hotel.setCountry(dto.country());

    HotelEntity saved = hotelRepository.save(hotel);
    hotelSearchService.index(saved);
    log.info("Hotel updated: id={}", saved.getId());
    return toDto(saved, false);
  }

  @Override
  @Transactional
  public void deleteHotel(Long hotelId) {
    validateId(hotelId);
    log.info("Deleting hotel id={}", hotelId);
    HotelEntity hotel =
        hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(hotelId));

    long activeReservations =
        hotel.getRooms().stream()
            .flatMap(r -> r.getReservations().stream())
            .filter(
                res ->
                    res.getReservationStatus() == ReservationStatus.PENDING
                        || res.getReservationStatus() == ReservationStatus.APPROVED)
            .count();

    if (activeReservations > 0) {
      log.warn("Cannot delete hotel id={}: {} active reservation(s)", hotelId, activeReservations);
      throw new HotelHasActiveRoomsException(hotelId, activeReservations);
    }

    hotelSearchService.delete(hotelId);
    hotelPopularityService.delete(hotelId);
    hotelRepository.delete(hotel);
    log.info("Hotel deleted: id={}", hotelId);
  }

  @Override
  @Transactional
  public void uploadMainPhoto(Long hotelId, MultipartFile file) {
    validateId(hotelId);
    if (file == null || file.isEmpty()) throw new EmptyFileException();
    log.info("Uploading main photo for hotel id={}", hotelId);
    HotelEntity hotel =
        hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(hotelId));
    String url = minioService.uploadHotelPhoto(file, hotelId);
    hotel.setMainPhotoUrl(url);
    HotelEntity saved = hotelRepository.save(hotel);
    hotelSearchService.index(saved);
    log.info("Main photo uploaded for hotel id={}: url={}", hotelId, url);
  }

  @Override
  @Transactional
  public void uploadGalleryPhotos(Long hotelId, List<MultipartFile> files) {
    validateId(hotelId);
    if (files == null || files.isEmpty()) throw new EmptyFileException();
    log.info("Uploading {} gallery photos for hotel id={}", files.size(), hotelId);
    HotelEntity hotel =
        hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(hotelId));
    List<String> urls = new ArrayList<>(hotel.getGalleryUrl());
    for (MultipartFile f : files) {
      urls.add(minioService.uploadHotelPhoto(f, hotelId));
    }
    hotel.setGalleryUrl(urls);
    HotelEntity saved = hotelRepository.save(hotel);
    hotelSearchService.index(saved);
    log.info("Gallery photos uploaded for hotel id={}", hotelId);
  }

  @Override
  @Transactional
  public void deleteGalleryPhoto(Long hotelId, int photoIndex) {
    validateId(hotelId);
    log.info("Deleting gallery photo index={} for hotel id={}", photoIndex, hotelId);
    HotelEntity hotel =
        hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(hotelId));
    List<String> gallery = new ArrayList<>(hotel.getGalleryUrl());
    if (photoIndex < 0 || photoIndex >= gallery.size()) {
      log.warn("Invalid photo index={} for hotel id={}", photoIndex, hotelId);
      throw new InvalidPhotoIdException();
    }
    String url = gallery.remove(photoIndex);
    minioService.deleteObject(url);
    hotel.setGalleryUrl(gallery);
    HotelEntity saved = hotelRepository.save(hotel);
    hotelSearchService.index(saved);
    log.info("Gallery photo deleted for hotel id={}", hotelId);
  }

  private void validateId(Long id) {
    if (id == null || id < 1) throw new InvalidHotelIdException();
  }

  private HotelDto toDto(HotelEntity h, boolean includeRooms) {
    Double avg = hotelRatingRepository.getAverageRating(h.getId());
    Long count = hotelRatingRepository.countByHotelId(h.getId());
    return new HotelDto(
        h.getId(),
        h.getName(),
        h.getDescription(),
        h.getAddress(),
        h.getMainPhotoUrl(),
        h.getGalleryUrl(),
        avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0,
        count != null ? count : 0L,
        h.getCountry(),
        includeRooms ? List.of() : null);
  }
}
