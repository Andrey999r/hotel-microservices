package com.andrey999r.staynova.rooms.dal;

import com.andrey999r.staynova.availability.dal.AvailabilityStatus;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

  @Query("SELECT r FROM RoomEntity r WHERE r.availabilityStatus = :availabilityStatus")
  List<RoomEntity> findAllAvailableRooms(
      @Param("availabilityStatus") AvailabilityStatus availabilityStatus, Pageable pageable);

  @Query(
      "SELECT r.availabilityStatus FROM RoomEntity r "
          + "WHERE r.availabilityStatus = :status AND r.id = :roomId")
  AvailabilityStatus getRoomAvailabilityStatus(
      @Param("status") AvailabilityStatus status, @Param("roomId") Long roomId);

  @Query(
      value =
          """
          SELECT * FROM room r
          WHERE (:roomId IS NULL OR r.id = :roomId)
          AND (:title IS NULL OR lower(r.title) LIKE lower(concat('%', :title, '%')))
          AND (:description IS NULL OR lower(r.description) LIKE lower(concat('%', :description, '%')))
          AND (:availabilityStatus IS NULL OR r.status = :availabilityStatus)
          AND (:price IS NULL OR r.price = :price)
          """,
      nativeQuery = true)
  List<RoomEntity> findAllByFilter(
      @Param("roomId") Long roomId,
      @Param("availabilityStatus") String availabilityStatus,
      @Param("title") String title,
      @Param("description") String description,
      @Param("price") BigDecimal price,
      Pageable pageable);

  @Query("SELECT r FROM RoomEntity r WHERE r.hotel.id = :hotelId")
  List<RoomEntity> findByHotelId(@Param("hotelId") Long hotelId);

  @Query(
      """
      SELECT r FROM RoomEntity r
      WHERE r.hotel.id = :hotelId
      AND (lower(r.title) LIKE :query OR lower(r.description) LIKE :query)
      """)
  List<RoomEntity> findByHotelIdAndQuery(
      @Param("hotelId") Long hotelId, @Param("query") String query);
}
