package com.andrey999r.staynova.reservations.api;

import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.reservations.api.dto.ReservationDto;
import com.andrey999r.staynova.reservations.api.dto.ReservationFilterDto;
import com.andrey999r.staynova.reservations.bl.ReservationService;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

  private static final String GET_BY_ID = "/{id}";
  private static final String GET_ALL = "";
  private static final String CREATE = "";
  private static final String DELETE_BY_ID = "/{id}";

  private final ReservationService reservationService;
  private final Mapper mapper;

  @GetMapping(GET_BY_ID)
  public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
    log.info("GET /reservations/{}", id);
    ReservationEntity entity = reservationService.getReservationById(id);
    return ResponseEntity.ok(mapper.reservationToDomain(entity));
  }

  @GetMapping(GET_ALL)
  public ResponseEntity<List<ReservationDto>> getReservations(
      @RequestParam(value = "roomId", required = false) Long roomId,
      @RequestParam(value = "pageSize", required = false) Integer pageSize,
      @RequestParam(value = "pageNumber", required = false) Integer pageNumber) {
    log.info(
        "GET /reservations roomId={}, pageSize={}, pageNumber={}", roomId, pageSize, pageNumber);
    ReservationFilterDto filter = new ReservationFilterDto(roomId, pageSize, pageNumber);
    List<ReservationEntity> entities = reservationService.getReservations(filter);
    List<ReservationDto> result = entities.stream().map(mapper::reservationToDomain).toList();
    log.info("GET /reservations returned {} reservations", result.size());
    return ResponseEntity.ok(result);
  }

  @PostMapping(CREATE)
  public ResponseEntity<ReservationDto> bookReservation(
      @Valid @RequestBody ReservationDto reservationToBook) {
    log.info(
        "POST /reservations roomId={}, from={}, to={}",
        reservationToBook.roomId(),
        reservationToBook.startDate(),
        reservationToBook.endDate());
    ReservationEntity booked = reservationService.bookReservation(reservationToBook);
    ReservationDto dto = mapper.reservationToDomain(booked);
    URI location = URI.create("/reservations/" + dto.id());
    log.info("POST /reservations → id={}, status=PENDING", dto.id());
    return ResponseEntity.created(location).body(dto);
  }

  @DeleteMapping(DELETE_BY_ID)
  public ResponseEntity<Void> cancelReservationById(@PathVariable Long id) {
    log.info("DELETE /reservations/{}", id);
    reservationService.cancelReservation(id);
    log.info("DELETE /reservations/{} → cancelled", id);
    return ResponseEntity.noContent().build();
  }
}
