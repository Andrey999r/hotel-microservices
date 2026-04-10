package com.andrey999r.staynova.reservations.bl;

import com.andrey999r.staynova.reservations.api.dto.ReservationDto;
import com.andrey999r.staynova.reservations.api.dto.ReservationFilterDto;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import java.util.List;

public interface ReservationService {

  ReservationEntity getReservationById(Long id);

  List<ReservationEntity> getReservations(ReservationFilterDto filter);

  ReservationEntity bookReservation(ReservationDto reservationToBook);

  void cancelReservation(Long id);
}
