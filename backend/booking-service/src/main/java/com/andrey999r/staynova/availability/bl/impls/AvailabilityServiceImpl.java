package com.andrey999r.staynova.availability.bl.impls;

import com.andrey999r.staynova.availability.api.dto.AvailabilityFilterDto;
import com.andrey999r.staynova.availability.api.dto.AvailabilityResponseDto;
import com.andrey999r.staynova.availability.api.dto.NotAvailableDatesDto;
import com.andrey999r.staynova.availability.bl.AvailabilityService;
import com.andrey999r.staynova.availability.dal.AvailabilityReservationRepository;
import com.andrey999r.staynova.availability.dal.AvailabilityStatus;
import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.general.exception.custom.availability.NoAvailableRoomsException;
import com.andrey999r.staynova.general.exception.custom.room.RoomNotFoundException;
import com.andrey999r.staynova.reservations.api.dto.ReservationDto;
import com.andrey999r.staynova.reservations.dal.ReservationStatus;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.dal.RoomRepository;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {
  private final RoomRepository roomRepository;
  private final AvailabilityReservationRepository availabilityReservationRepository;
  private final Mapper mapper;

  @Override
  public boolean isAvailable(ReservationDto reservationToCheck) {
    log.info(
        "Checking availability for reservation: roomId={}, from={}, to={}",
        reservationToCheck.roomId(),
        reservationToCheck.startDate(),
        reservationToCheck.endDate());
    List<Long> allConflictingIds =
        availabilityReservationRepository.findAllConflictingIds(
            reservationToCheck.roomId(),
            reservationToCheck.startDate(),
            reservationToCheck.endDate(),
            reservationToCheck.id() != null ? reservationToCheck.id() : -1L);
    if (allConflictingIds.isEmpty()) {
      log.info("Room is available: roomId={}", reservationToCheck.roomId());
      return true;
    }
    log.info(
        "Room is unavailable: roomId={}, conflicts with approved reservations: {}",
        reservationToCheck.roomId(),
        allConflictingIds);
    return false;
  }

  @Override
  public List<RoomDto> getAllAvailableRooms(AvailabilityFilterDto availabilityFilterDto) {
    log.info(
        "Getting available rooms: page={}, size={}",
        availabilityFilterDto.pageNumber(),
        availabilityFilterDto.pageSize());

    Integer pageSize =
        availabilityFilterDto.pageSize() != null ? availabilityFilterDto.pageSize() : 10;
    Integer pageNumber =
        availabilityFilterDto.pageNumber() != null ? availabilityFilterDto.pageNumber() : 0;
    Pageable pageConfig = Pageable.ofSize(pageSize).withPage(pageNumber);

    List<RoomEntity> allAvailableRooms =
        roomRepository.findAllAvailableRooms(AvailabilityStatus.AVAILABLE, pageConfig);
    if (allAvailableRooms.isEmpty()) {
      log.warn("No available rooms found");
      throw new NoAvailableRoomsException();
    }

    List<RoomDto> mappedRooms = new ArrayList<>();
    for (RoomEntity room : allAvailableRooms) {
      mappedRooms.add(mapper.roomToDomain(room));
    }
    log.info("Returning {} available rooms", mappedRooms.size());
    return mappedRooms;
  }

  @Override
  public AvailabilityResponseDto checkIsRoomAvailable(Long roomId) {
    log.info("Checking availability status for room id={}", roomId);
    if (roomRepository.findById(roomId).isEmpty()) {
      log.warn("Room not found: id={}", roomId);
      throw new RoomNotFoundException(roomId);
    }
    AvailabilityStatus availabilityStatus =
        roomRepository.getRoomAvailabilityStatus(AvailabilityStatus.AVAILABLE, roomId);
    AvailabilityResponseDto response;
    if (!availabilityStatus.equals(AvailabilityStatus.AVAILABLE)) {
      log.info("Room id={} is UNAVAILABLE", roomId);
      response =
          new AvailabilityResponseDto(
              "Room is unavailable, please try again later", AvailabilityStatus.UNAVAILABLE);
    } else {
      log.info("Room id={} is AVAILABLE", roomId);
      response =
          new AvailabilityResponseDto(
              "Room is available, do you want to create a reservation?",
              AvailabilityStatus.AVAILABLE);
    }
    return response;
  }

  @Override
  public List<NotAvailableDatesDto> getNotAvailableDays(Long roomId) {
    log.info("Getting unavailable dates for room id={}", roomId);
    RoomEntity room =
        roomRepository
            .findById(roomId)
            .orElseThrow(
                () -> {
                  log.warn("Room not found: id={}", roomId);
                  return new RoomNotFoundException(roomId);
                });
    List<ReservationEntity> allReservationsByRoom = room.getReservations();
    if (allReservationsByRoom.isEmpty()) {
      log.info("No reservations for room id={}", roomId);
      return Collections.emptyList();
    }
    List<NotAvailableDatesDto> allDates = new ArrayList<>();
    for (ReservationEntity reservation : allReservationsByRoom) {
      if (reservation.getReservationStatus() != ReservationStatus.CANCELLED) {
        allDates.add(
            new NotAvailableDatesDto(reservation.getStartDate(), reservation.getEndDate()));
      }
    }
    log.info("Returning {} unavailable date ranges for room id={}", allDates.size(), roomId);
    return allDates;
  }
}
