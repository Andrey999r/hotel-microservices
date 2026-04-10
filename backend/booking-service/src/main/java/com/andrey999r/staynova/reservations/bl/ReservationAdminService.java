package com.andrey999r.staynova.reservations.bl;

import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import java.util.List;

public interface ReservationAdminService {

  List<ReservationEntity> getAllReservations(Integer pageSize, Integer pageNumber);
}
