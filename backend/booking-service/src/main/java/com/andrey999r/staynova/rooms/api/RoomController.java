package com.andrey999r.staynova.rooms.api;

import com.andrey999r.staynova.availability.dal.AvailabilityStatus;
import com.andrey999r.staynova.rooms.api.dto.PopularFilterDto;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.api.dto.RoomFilterDto;
import com.andrey999r.staynova.rooms.bl.RoomService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RoomController.BASE_PATH)
@RequiredArgsConstructor
@Slf4j
public class RoomController {

  public static final String BASE_PATH = "/rooms";
  public static final String SEARCH = "/search";
  public static final String ALL = "";
  public static final String POPULAR = "/popular";
  public static final String GET_BY_ID = "/{id}";
  public static final String GET_PHOTO = "/{roomId}/{photoId}";

  private final RoomService roomService;

  @GetMapping(SEARCH)
  public ResponseEntity<List<RoomDto>> search(@RequestParam String query) {
    log.info("GET /rooms/search query='{}'", query);
    List<RoomDto> result = roomService.search(query);
    log.info("GET /rooms/search returned {} results", result.size());
    return ResponseEntity.ok(result);
  }

  @GetMapping(ALL)
  public ResponseEntity<List<RoomDto>> getAll(
      @RequestParam(value = "roomId", required = false) Long roomId,
      @RequestParam(value = "availability", required = false) AvailabilityStatus availabilityStatus,
      @RequestParam(value = "title", required = false) String title,
      @RequestParam(value = "description", required = false) String description,
      @RequestParam(value = "price", required = false) BigDecimal price,
      @RequestParam(value = "pageSize", required = false) Integer pageSize,
      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
      @RequestParam(required = false) String sortByPrice) {
    log.info(
        "GET /rooms pageSize={}, pageNumber={}, sortByPrice={}", pageSize, pageNumber, sortByPrice);
    RoomFilterDto filter =
        new RoomFilterDto(
            roomId,
            availabilityStatus,
            price,
            title,
            description,
            pageSize,
            pageNumber,
            sortByPrice);
    List<RoomDto> result = roomService.getAllRoomsByFilter(filter);
    log.info("GET /rooms returned {} rooms", result.size());
    return ResponseEntity.ok(result);
  }

  @GetMapping(GET_BY_ID)
  public ResponseEntity<RoomDto> getRoomById(@PathVariable("id") long roomId) {
    log.info("GET /rooms/{}", roomId);
    RoomDto room = roomService.getRoomById(roomId);
    return ResponseEntity.ok(room);
  }

  @GetMapping(GET_PHOTO)
  public ResponseEntity<String> getPhotoUrl(
      @PathVariable("roomId") long roomId, @PathVariable("photoId") long photoId) {
    log.info("GET /rooms/{}/{}", roomId, photoId);
    String url = roomService.getPhotoById(roomId, photoId);
    return ResponseEntity.ok(url);
  }

  @GetMapping(POPULAR)
  public ResponseEntity<List<RoomDto>> getPopular(
      @RequestParam("pageSize") Integer pageSize, @RequestParam("pageNumber") Integer pageNumber) {
    log.info("GET /rooms/popular pageSize={}, pageNumber={}", pageSize, pageNumber);
    List<RoomDto> result =
        roomService.getAllPopularByFilter(new PopularFilterDto(pageSize, pageNumber));
    log.info("GET /rooms/popular returned {} rooms", result.size());
    return ResponseEntity.ok(result);
  }
}
