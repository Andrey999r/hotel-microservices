package com.andrey999r.staynova.hotel.bl;

import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;
import com.andrey999r.staynova.general.kafka.notification.NotificationPublisher;
import com.andrey999r.staynova.hotel.dal.HotelRatingRepository;
import com.andrey999r.staynova.reservations.dal.ReservationRepository;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewReminderScheduler {

  private final ReservationRepository reservationRepository;
  private final HotelRatingRepository hotelRatingRepository;
  private final NotificationPublisher notificationProducer;

  @Scheduled(cron = "0 0 10 * * *")
  @Transactional
  public void sendReviewReminders() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    log.info("Running review reminder scheduler for checkout date={}", yesterday);

    List<ReservationEntity> endedReservations =
        reservationRepository.findApprovedEndingOn(yesterday);

    log.info("Found {} reservations that ended on {}", endedReservations.size(), yesterday);

    for (ReservationEntity reservation : endedReservations) {
      String userLogin = reservation.getUserLogin();
      Long hotelId = reservation.getRoom().getHotel().getId();
      String hotelName = reservation.getRoom().getHotel().getName();

      if (hotelRatingRepository.existsByHotelIdAndUserLogin(hotelId, userLogin)) {
        log.debug(
            "Skipping review reminder for user='{}', hotel='{}' — already reviewed",
            userLogin,
            hotelName);
        continue;
      }

      String message =
          "Вы завершили проживание в «"
              + hotelName
              + "». Поделитесь впечатлениями — ваш отзыв поможет другим гостям!";

      notificationProducer.send(userLogin, message, NotificationEventType.REVIEW_REQUEST);

      log.info(
          "Sent review reminder to user='{}' for hotel='{}' (id={})",
          userLogin,
          hotelName,
          hotelId);
    }
  }
}
