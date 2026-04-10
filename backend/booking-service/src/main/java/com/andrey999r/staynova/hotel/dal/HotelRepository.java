package com.andrey999r.staynova.hotel.dal;

import com.andrey999r.staynova.hotel.Country;
import com.andrey999r.staynova.hotel.dal.entities.HotelEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long> {

  @Query(
      "SELECT DISTINCT h FROM HotelEntity h "
          + "WHERE (:country IS NULL OR h.country = :country) "
          + "AND EXISTS ("
          + "  SELECT r FROM RoomEntity r "
          + "  WHERE r.hotel = h "
          + "  AND r.availabilityStatus = 'AVAILABLE' "
          + "  AND (:guests IS NULL OR r.maxGuests >= :guests) "
          + "  AND NOT EXISTS ("
          + "    SELECT res FROM ReservationEntity res "
          + "    WHERE res.room = r "
          + "    AND res.reservationStatus IN ('PENDING', 'APPROVED') "
          + "    AND res.startDate < :checkOut "
          + "    AND res.endDate > :checkIn "
          + "  )"
          + ")")
  List<HotelEntity> findAvailableHotels(
      @Param("country") Country country,
      @Param("guests") Integer guests,
      @Param("checkIn") LocalDate checkIn,
      @Param("checkOut") LocalDate checkOut,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT h FROM HotelEntity h " + "WHERE (:country IS NULL OR h.country = :country)")
  List<HotelEntity> findAllByCountry(@Param("country") Country country, Pageable pageable);

  boolean existsByName(String name);
}
