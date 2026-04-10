package com.andrey999r.staynova.reservations.bl.impls.admin;

import com.andrey999r.staynova.reservations.api.dto.ReservationFilterDto;
import com.andrey999r.staynova.reservations.bl.ReservationAdminService;
import com.andrey999r.staynova.reservations.bl.ReservationService;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminServiceImpl implements ReservationAdminService {

  private final ReservationService reservationService;

  @Override
  public List<ReservationEntity> getAllReservations(Integer pageSize, Integer pageNumber) {
    log.info("Admin: getting all reservations pageSize={}, pageNumber={}", pageSize, pageNumber);
    return reservationService.getReservations(new ReservationFilterDto(null, pageSize, pageNumber));
  }
}
