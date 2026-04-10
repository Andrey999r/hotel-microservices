package com.andrey999r.staynova.hotel.dal;

import com.andrey999r.staynova.hotel.dal.entities.HotelRatingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRatingRepository extends JpaRepository<HotelRatingEntity, Long> {

  boolean existsByHotelIdAndUserLogin(Long hotelId, String userLogin);

  Optional<HotelRatingEntity> findByHotelIdAndUserLogin(Long hotelId, String userLogin);

  @Query(
      "SELECT COALESCE(AVG(CAST(r.rating AS double)), 0.0) FROM HotelRatingEntity r WHERE r.hotel.id = :hotelId")
  Double getAverageRating(@Param("hotelId") Long hotelId);

  @Query("SELECT COUNT(r) FROM HotelRatingEntity r WHERE r.hotel.id = :hotelId")
  Long countByHotelId(@Param("hotelId") Long hotelId);

  @Query(
      value =
          """
      SELECT hr.user_login,
             r.title             AS room_title,
             DATEDIFF(latest.end_date, latest.start_date) AS nights,
             hr.rating,
             hr.comment,
             hr.created_at
      FROM hotel_rating hr
      LEFT JOIN (
          SELECT res.user_login,
                 ro.hotel_id,
                 res.room_id,
                 res.end_date_of_reservation   AS end_date,
                 res.start_date_of_reservation AS start_date,
                 ROW_NUMBER() OVER (
                     PARTITION BY res.user_login, ro.hotel_id
                     ORDER BY res.end_date_of_reservation DESC
                 ) AS rn
          FROM reservation res
          INNER JOIN room ro ON ro.id = res.room_id
      ) latest ON latest.user_login = hr.user_login
               AND latest.hotel_id  = hr.hotel_id
               AND latest.rn        = 1
      LEFT JOIN room r ON r.id = latest.room_id
      WHERE hr.hotel_id = :hotelId
      ORDER BY hr.created_at DESC
      """,
      nativeQuery = true)
  List<Object[]> findReviewsByHotelId(@Param("hotelId") Long hotelId);
}
