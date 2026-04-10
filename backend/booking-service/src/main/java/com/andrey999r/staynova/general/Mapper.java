package com.andrey999r.staynova.general;

import com.andrey999r.staynova.reservations.api.dto.ReservationDto;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

  public ReservationDto reservationToDomain(ReservationEntity reservation) {
    return new ReservationDto(
        reservation.getId(),
        reservation.getUserLogin(),
        reservation.getRoom().getId(),
        reservation.getStartDate(),
        reservation.getEndDate(),
        reservation.getReservationStatus(),
        reservation.getPaymentUrl());
  }

  public ReservationEntity reservationToEntity(ReservationDto reservation, RoomEntity room) {
    return new ReservationEntity(
        reservation.id(),
        reservation.userLogin(),
        room,
        reservation.startDate(),
        reservation.endDate(),
        reservation.reservationStatus(),
        reservation.paymentUrl());
  }

  public RoomDto roomToDomain(RoomEntity roomEntity) {
    return new RoomDto(
        roomEntity.getId(),
        roomEntity.getRoomType(),
        roomEntity.getAvailabilityStatus(),
        roomEntity.getMainPhotoUrl(),
        roomEntity.getPhotoUrls(),
        roomEntity.getDescription(),
        roomEntity.getTitle(),
        roomEntity.getPrice(),
        roomEntity.getCurrency(),
        roomEntity.getMaxGuests(),
        roomEntity.getHotel() != null ? roomEntity.getHotel().getId() : null);
  }

  public RoomEntity roomToEntity(RoomDto roomDto) {
    RoomEntity entity =
        new RoomEntity(
            roomDto.roomType(),
            roomDto.availabilityStatus(),
            roomDto.id(),
            roomDto.mainPhotoUrl(),
            roomDto.description(),
            roomDto.title(),
            roomDto.price(),
            roomDto.photoUrls(),
            roomDto.currency());
    entity.setMaxGuests(roomDto.maxGuests() != null ? roomDto.maxGuests() : 1);
    return entity;
  }
}
