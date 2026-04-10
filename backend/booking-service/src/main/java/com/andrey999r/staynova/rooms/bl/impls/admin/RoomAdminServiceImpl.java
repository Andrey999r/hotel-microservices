package com.andrey999r.staynova.rooms.bl.impls.admin;

import com.andrey999r.staynova.availability.dal.AvailabilityReservationRepository;
import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.general.exception.custom.room.EmptyFileException;
import com.andrey999r.staynova.general.exception.custom.room.EmptyRoomListException;
import com.andrey999r.staynova.general.exception.custom.room.InvalidPhotoIdException;
import com.andrey999r.staynova.general.exception.custom.room.InvalidRoomIdException;
import com.andrey999r.staynova.general.exception.custom.room.RoomHasActiveReservationsException;
import com.andrey999r.staynova.general.exception.custom.room.RoomNotFoundException;
import com.andrey999r.staynova.general.minio.ObjectStorageService;
import com.andrey999r.staynova.hotel.dal.HotelRepository;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.bl.PopularityService;
import com.andrey999r.staynova.rooms.bl.RoomAdminService;
import com.andrey999r.staynova.rooms.bl.RoomSearchService;
import com.andrey999r.staynova.rooms.dal.Currency;
import com.andrey999r.staynova.rooms.dal.RoomRepository;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomAdminServiceImpl implements RoomAdminService {

  private static final Currency DEFAULT_CURRENCY = Currency.RUB;
  private final RoomRepository roomRepository;
  private final HotelRepository hotelRepository;
  private final Mapper mapper;
  private final ObjectStorageService minioService;
  private final RoomSearchService roomSearchService;
  private final PopularityService popularityService;
  private final AvailabilityReservationRepository availabilityReservationRepository;

  @Override
  public RoomEntity createRoom(RoomDto roomDto) {
    log.info("Creating room: title={}, price={}", roomDto.title(), roomDto.price());
    RoomEntity roomToSave = mapper.roomToEntity(roomDto);
    roomToSave.setMainPhotoUrl(minioService.getDefaultPicture());
    roomToSave.setPhotoUrls(List.of());
    roomToSave.setDescription(roomDto.description());
    roomToSave.setTitle(roomDto.title());
    roomToSave.setPrice(roomDto.price());
    roomToSave.setCurrency(roomDto.currency() != null ? roomDto.currency() : DEFAULT_CURRENCY);
    if (roomDto.hotelId() != null) {
      hotelRepository.findById(roomDto.hotelId()).ifPresent(roomToSave::setHotel);
    }
    RoomEntity savedRoom = roomRepository.save(roomToSave);
    roomSearchService.index(savedRoom);
    popularityService.init(savedRoom.getId());
    log.info("Room created successfully: id={}, title={}", savedRoom.getId(), savedRoom.getTitle());
    return savedRoom;
  }

  @Override
  public void createMany(@Valid List<RoomDto> roomDtos) {
    if (roomDtos == null || roomDtos.isEmpty()) {
      log.warn("Attempt to create empty room list");
      throw new EmptyRoomListException();
    }
    log.info("Creating {} rooms in bulk", roomDtos.size());
    for (RoomDto roomDto : roomDtos) {
      createRoom(roomDto);
    }
    log.info("Bulk room creation completed: {} rooms created", roomDtos.size());
  }

  @Override
  public void deleteById(long roomId) {
    if (roomId < 1) {
      log.warn("Invalid room id for deletion: {}", roomId);
      throw new InvalidRoomIdException();
    }
    log.info("Deleting room id={}", roomId);
    long activeReservations = availabilityReservationRepository.countActiveByRoomId(roomId);
    if (activeReservations > 0) {
      log.warn(
          "Cannot delete room id={}: {} active reservations exist", roomId, activeReservations);
      throw new RoomHasActiveReservationsException(roomId, activeReservations);
    }
    popularityService.delete(roomId);
    roomRepository.deleteById(roomId);
    log.info("Room deleted successfully: id={}", roomId);
  }

  @Override
  public void uploadPhoto(MultipartFile file, long roomId) {
    if (roomId < 1) {
      log.warn("Invalid room id for photo upload: {}", roomId);
      throw new InvalidRoomIdException();
    }
    if (file.isEmpty()) {
      log.warn("Empty file provided for room id={}", roomId);
      throw new EmptyFileException();
    }
    log.info("Uploading main photo for room id={}, file={}", roomId, file.getOriginalFilename());
    RoomEntity room =
        roomRepository
            .findById(roomId)
            .orElseThrow(
                () -> {
                  log.warn("Room not found for photo upload: id={}", roomId);
                  return new RoomNotFoundException(roomId);
                });
    String photoUrl = minioService.uploadRoomPhoto(file, roomId);
    room.setMainPhotoUrl(photoUrl);
    RoomEntity savedRoom = roomRepository.save(room);
    roomSearchService.index(savedRoom);
    log.info("Main photo uploaded for room id={}: url={}", roomId, photoUrl);
  }

  @Override
  public void uploadPhoto(List<MultipartFile> files, long roomId) {
    if (roomId < 1) {
      log.warn("Invalid room id for photo upload: {}", roomId);
      throw new InvalidRoomIdException();
    }
    if (files.isEmpty()) {
      log.warn("Empty file list provided for room id={}", roomId);
      throw new EmptyFileException();
    }
    log.info("Uploading {} photos for room id={}", files.size(), roomId);
    RoomEntity room =
        roomRepository
            .findById(roomId)
            .orElseThrow(
                () -> {
                  log.warn("Room not found for photos upload: id={}", roomId);
                  return new RoomNotFoundException(roomId);
                });
    List<String> photos = new ArrayList<>();
    for (MultipartFile file : files) {
      String photoUrl = minioService.uploadRoomPhoto(file, roomId);
      if (files.getFirst().equals(file)) {
        room.setMainPhotoUrl(photoUrl);
        continue;
      }
      photos.add(photoUrl);
    }
    room.setPhotoUrls(photos);
    RoomEntity savedRoom = roomRepository.save(room);
    roomSearchService.index(savedRoom);
    log.info("Photos uploaded for room id={}: main + {} gallery", roomId, photos.size());
  }

  @Override
  public void deletePhoto(long roomId, long photoId) {
    if (roomId < 1 || photoId < 1) {
      log.warn("Invalid roomId={} or photoId={} for photo deletion", roomId, photoId);
      throw new InvalidRoomIdException();
    }
    log.info("Deleting photo photoId={} from room id={}", photoId, roomId);
    RoomEntity room =
        roomRepository
            .findById(roomId)
            .orElseThrow(
                () -> {
                  log.warn("Room not found for photo deletion: id={}", roomId);
                  return new RoomNotFoundException(roomId);
                });
    String defaultUrl = minioService.getDefaultPicture();

    if (photoId == 1) {
      String mainUrl = room.getMainPhotoUrl();
      if (!mainUrl.equals(defaultUrl)) {
        minioService.deleteObject(mainUrl);
      }
      List<String> others = new ArrayList<>(room.getPhotoUrls());
      if (!others.isEmpty()) {
        room.setMainPhotoUrl(others.removeFirst());
        room.setPhotoUrls(others);
      } else {
        room.setMainPhotoUrl(defaultUrl);
      }
    } else {
      List<String> photos = new ArrayList<>(room.getPhotoUrls());
      int index = (int) (photoId - 2);
      if (index < 0 || index >= photos.size()) {
        log.warn(
            "Photo not found: photoId={} for room id={}, gallery size={}",
            photoId,
            roomId,
            photos.size());
        throw new InvalidPhotoIdException();
      }
      minioService.deleteObject(photos.get(index));
      photos.remove(index);
      room.setPhotoUrls(photos);
    }
    RoomEntity savedRoom = roomRepository.save(room);
    roomSearchService.index(savedRoom);
    log.info("Photo photoId={} deleted from room id={}", photoId, roomId);
  }
}
