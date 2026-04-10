package com.andrey999r.staynova.availability.dal;

import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityReservationRepository extends JpaRepository<ReservationEntity, Long> {
  @Query(
      """
            select r.id from ReservationEntity r
            where r.room.id = :roomId
            and r.startDate < :endDate
            and r.endDate > :startDate
            and r.reservationStatus = 'APPROVED'
            and r.id != :excludeId
            """)
  List<Long> findAllConflictingIds(
      @Param("roomId") Long roomId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("excludeId") Long excludeId);

  @Query(
      """
            select count(r) from ReservationEntity r
            where r.room.id = :roomId
            and r.reservationStatus != 'CANCELLED'
            """)
  long countActiveByRoomId(@Param("roomId") Long roomId);
}
