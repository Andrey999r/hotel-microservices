package com.andrey999r.staynova.availability.bl;

import com.andrey999r.staynova.availability.api.dto.AvailabilityFilterDto;
import com.andrey999r.staynova.availability.api.dto.AvailabilityResponseDto;
import com.andrey999r.staynova.availability.api.dto.NotAvailableDatesDto;
import com.andrey999r.staynova.reservations.api.dto.ReservationDto;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import java.util.List;

public interface AvailabilityService {

  boolean isAvailable(ReservationDto reservationToCheck);

  List<RoomDto> getAllAvailableRooms(AvailabilityFilterDto availabilityFilterDto);

  AvailabilityResponseDto checkIsRoomAvailable(Long roomId);

  List<NotAvailableDatesDto> getNotAvailableDays(Long roomId);
}
