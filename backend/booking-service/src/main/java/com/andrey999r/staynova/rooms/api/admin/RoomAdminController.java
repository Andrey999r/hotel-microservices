package com.andrey999r.staynova.rooms.api.admin;

import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.bl.RoomAdminService;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/rooms")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class RoomAdminController {

  private static final String CREATE_ONE = "";
  private static final String CREATE_MANY = "/batch";
  private static final String DELETE_BY_ID = "/{id}";
  private static final String UPLOAD_MAIN = "/{id}/main";
  private static final String UPLOAD_GALLERY = "/{id}/gallery";
  private static final String DELETE_PHOTO = "/{roomId}/photos/{photoId}";

  private final RoomAdminService roomAdminService;
  private final Mapper mapper;

  @PostMapping(CREATE_ONE)
  public ResponseEntity<RoomDto> create(@RequestBody @Valid RoomDto roomDto) {
    log.info("POST /admin/rooms title='{}'", roomDto.title());
    RoomEntity created = roomAdminService.createRoom(roomDto);
    RoomDto dto = mapper.roomToDomain(created);
    URI location = URI.create("/rooms/" + dto.id());
    log.info("POST /admin/rooms → id={}, location={}", dto.id(), location);
    return ResponseEntity.created(location).body(dto);
  }

  @PostMapping(CREATE_MANY)
  public ResponseEntity<Void> createMany(@RequestBody @Valid List<RoomDto> roomDtos) {
    log.info("POST /admin/rooms/batch count={}", roomDtos.size());
    roomAdminService.createMany(roomDtos);
    log.info("POST /admin/rooms/batch → {} rooms created", roomDtos.size());
    return ResponseEntity.status(201).build();
  }

  @DeleteMapping(DELETE_BY_ID)
  public ResponseEntity<Void> deleteRoomById(@PathVariable("id") long roomId) {
    log.info("DELETE /admin/rooms/{}", roomId);
    roomAdminService.deleteById(roomId);
    log.info("DELETE /admin/rooms/{} → deleted", roomId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(path = UPLOAD_MAIN, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadMainPhoto(
      @RequestPart("file") MultipartFile file, @PathVariable("id") long roomId) {
    log.info("POST /admin/rooms/{}/main file='{}'", roomId, file.getOriginalFilename());
    roomAdminService.uploadPhoto(file, roomId);
    URI location = URI.create("/rooms/" + roomId);
    log.info("POST /admin/rooms/{}/main → uploaded, location={}", roomId, location);
    return ResponseEntity.noContent().location(location).build();
  }

  @PostMapping(path = UPLOAD_GALLERY, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadGallery(
      @RequestPart("files") List<MultipartFile> files, @PathVariable("id") long roomId) {
    log.info("POST /admin/rooms/{}/gallery filesCount={}", roomId, files.size());
    roomAdminService.uploadPhoto(files, roomId);
    URI location = URI.create("/rooms/" + roomId);
    log.info("POST /admin/rooms/{}/gallery → uploaded, location={}", roomId, location);
    return ResponseEntity.noContent().location(location).build();
  }

  @DeleteMapping(DELETE_PHOTO)
  public ResponseEntity<Void> deletePhoto(
      @PathVariable("roomId") long roomId, @PathVariable("photoId") long photoId) {
    log.info("DELETE /admin/rooms/{}/photos/{}", roomId, photoId);
    roomAdminService.deletePhoto(roomId, photoId);
    log.info("DELETE /admin/rooms/{}/photos/{} → deleted", roomId, photoId);
    return ResponseEntity.noContent().build();
  }
}
