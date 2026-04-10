package com.andrey999r.staynova.availability.api;

import com.andrey999r.staynova.availability.api.dto.AvailabilityFilterDto;
import com.andrey999r.staynova.availability.api.dto.AvailabilityResponseDto;
import com.andrey999r.staynova.availability.api.dto.NotAvailableDatesDto;
import com.andrey999r.staynova.availability.bl.AvailabilityService;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/availability")
@RequiredArgsConstructor
@Slf4j
public class AvailabilityController {

  public static final String ROOMS_ALL = "/rooms";
  public static final String ROOM_BY_ID = "/rooms/{id}";
  public static final String NOT_AVAILABLE_DATES = "/reservations/notAvailableDates/{id}";

  private final AvailabilityService availabilityService;

  @GetMapping(ROOMS_ALL)
  public ResponseEntity<List<RoomDto>> getAllAvailableRooms(
      @RequestParam(value = "pageSize", required = false) Integer pageSize,
      @RequestParam(value = "pageNum", required = false) Integer pageNumber) {
    log.info("GET /availability/rooms pageSize={}, pageNumber={}", pageSize, pageNumber);
    AvailabilityFilterDto filter = new AvailabilityFilterDto(pageSize, pageNumber);
    List<RoomDto> result = availabilityService.getAllAvailableRooms(filter);
    log.info("GET /availability/rooms returned {} rooms", result.size());
    return ResponseEntity.ok(result);
  }

  @GetMapping(ROOM_BY_ID)
  public ResponseEntity<AvailabilityResponseDto> checkAvailability(@NotNull @PathVariable Long id) {
    log.info("GET /availability/rooms/{}", id);
    AvailabilityResponseDto response = availabilityService.checkIsRoomAvailable(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping(NOT_AVAILABLE_DATES)
  public ResponseEntity<List<NotAvailableDatesDto>> getNotAvailableDates(
      @NotNull @PathVariable Long id) {
    log.info("GET /availability/reservations/notAvailableDates/{}", id);
    List<NotAvailableDatesDto> dates = availabilityService.getNotAvailableDays(id);
    log.info(
        "GET /availability/reservations/notAvailableDates/{} → {} date ranges", id, dates.size());
    return ResponseEntity.ok(dates);
  }
}
