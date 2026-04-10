package com.andrey999r.staynova.reservations.api.admin;

import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.reservations.api.dto.ReservationDto;
import com.andrey999r.staynova.reservations.bl.ReservationAdminService;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ReservationAdminController {

  private final ReservationAdminService reservationAdminService;
  private final Mapper mapper;

  @GetMapping()
  public ResponseEntity<List<ReservationDto>> getAllReservations(
      @RequestParam(value = "pageSize", required = false) Integer pageSize,
      @RequestParam(value = "pageNumber", required = false) Integer pageNumber) {
    log.info("GET /admin/reservations pageSize={}, pageNumber={}", pageSize, pageNumber);
    List<ReservationEntity> result =
        reservationAdminService.getAllReservations(pageSize, pageNumber);
    List<ReservationDto> dtoResult = result.stream().map(mapper::reservationToDomain).toList();
    log.info("GET /admin/reservations returned {} reservations", result.size());
    return ResponseEntity.ok(dtoResult);
  }
}
