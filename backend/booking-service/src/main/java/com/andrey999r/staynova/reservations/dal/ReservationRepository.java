package com.andrey999r.staynova.reservations.dal;

import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
  @Transactional
  @Modifying
  @Query(
      "update ReservationEntity r "
          + "set r.reservationStatus = :reservationStatus "
          + "where r.id = :id")
  void setStatus(@Param("id") Long id, @Param("reservationStatus") ReservationStatus status);

  @Transactional
  @Modifying
  @Query("update ReservationEntity r set r.paymentUrl = :paymentUrl where r.id = :id")
  void setPaymentUrl(@Param("id") Long id, @Param("paymentUrl") String paymentUrl);

  @Query(
      """
            select r from ReservationEntity r
            where (:roomId is null or r.room.id = :roomId)
            and (:userLogin is null or r.userLogin = :userLogin)
            """)
  List<ReservationEntity> findAllbyFilter(
      @Param("roomId") Long roomId, @Param("userLogin") String userLogin, Pageable pageConfig);

  @Query(
      """
      SELECT COUNT(r) > 0
      FROM ReservationEntity r
      WHERE r.userLogin = :userLogin
        AND r.room.hotel.id = :hotelId
        AND r.reservationStatus = 'APPROVED'
        AND r.endDate <= :today
      """)
  boolean existsCompletedStay(
      @Param("userLogin") String userLogin,
      @Param("hotelId") Long hotelId,
      @Param("today") LocalDate today);

  @Query(
      """
      SELECT r FROM ReservationEntity r
      JOIN FETCH r.room room
      JOIN FETCH room.hotel
      WHERE r.reservationStatus = 'APPROVED'
        AND r.endDate = :date
      """)
  List<ReservationEntity> findApprovedEndingOn(@Param("date") LocalDate date);

  @Query(
      "SELECT r FROM ReservationEntity r "
          + "WHERE r.room.id = :roomId "
          + "AND r.reservationStatus = 'APPROVED' "
          + "AND ((r.startDate <= :endDate AND r.endDate >= :startDate)) "
          + "AND (:excludeId IS NULL OR r.id != :excludeId)")
  List<ReservationEntity> findApprovedConflicts(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("roomId") Long roomId,
      @Param("excludeId") Long excludeReservationId);

  @Query(
      "SELECT r FROM ReservationEntity r "
          + "WHERE r.room.id = :roomId "
          + "AND r.reservationStatus IN ('PENDING', 'APPROVED') "
          + "AND r.startDate < :endDate AND r.endDate > :startDate "
          + "AND (:excludeId IS NULL OR r.id != :excludeId)")
  List<ReservationEntity> findActiveConflicts(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("roomId") Long roomId,
      @Param("excludeId") Long excludeReservationId);
}
