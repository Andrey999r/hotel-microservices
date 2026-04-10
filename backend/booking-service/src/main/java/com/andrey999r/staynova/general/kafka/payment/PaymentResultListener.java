package com.andrey999r.staynova.general.kafka.payment;

import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;
import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import com.andrey999r.staynova.general.kafka.notification.NotificationProducer;
import com.andrey999r.staynova.general.kafka.payment.dto.PaymentCallbackKafkaDto;
import com.andrey999r.staynova.reservations.dal.ReservationRepository;
import com.andrey999r.staynova.reservations.dal.ReservationStatus;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentResultListener {

  private final ReservationRepository reservationRepository;
  private final NotificationProducer notificationProducer;

  @KafkaListener(topics = KafkaTopics.PAYMENT_RESULT_TOPIC, groupId = "booking-service-group")
  @Transactional
  public void handlePaymentCallback(PaymentCallbackKafkaDto dto) {
    log.info(
        "Received payment callback: reservationId={}, paymentUrl={}, success={}",
        dto.reservationId(),
        dto.paymentUrl(),
        dto.success());

    if (dto.paymentUrl() != null) {
      handlePaymentUrlReady(dto);
    } else {
      handlePaymentResult(dto);
    }
  }

  private void handlePaymentUrlReady(PaymentCallbackKafkaDto dto) {
    ReservationEntity reservation = findReservation(dto.reservationId());
    reservation.setPaymentUrl(dto.paymentUrl());
    reservationRepository.save(reservation);
    log.info("Payment URL saved for reservationId={}: {}", dto.reservationId(), dto.paymentUrl());
  }

  private void handlePaymentResult(PaymentCallbackKafkaDto dto) {
    ReservationEntity reservation = findReservation(dto.reservationId());

    if (dto.success()) {
      reservation.setReservationStatus(ReservationStatus.APPROVED);
      reservationRepository.save(reservation);

      notificationProducer.send(
          reservation.getUserLogin(),
          "Ваша бронь «"
              + reservation.getRoom().getTitle()
              + "» с "
              + reservation.getStartDate()
              + " по "
              + reservation.getEndDate()
              + " подтверждена. Оплата прошла успешно!",
          NotificationEventType.BOOKING_APPROVED);

      log.info("Reservation id={} approved after successful payment", dto.reservationId());
    } else {
      String userLogin = reservation.getUserLogin();
      String roomTitle = reservation.getRoom().getTitle();

      reservationRepository.deleteById(dto.reservationId());

      notificationProducer.send(
          userLogin,
          "Оплата брони «"
              + roomTitle
              + "» не прошла"
              + (dto.errorMessage() != null ? ": " + dto.errorMessage() : ".")
              + " Попробуйте снова.",
          NotificationEventType.BOOKING_CANCELLED);

      log.warn(
          "Reservation id={} deleted due to failed payment. Reason: {}",
          dto.reservationId(),
          dto.errorMessage());
    }
  }

  private ReservationEntity findReservation(long reservationId) {
    return reservationRepository
        .findById(reservationId)
        .orElseThrow(
            () -> {
              log.error("Reservation not found for payment callback: id={}", reservationId);
              return new IllegalStateException(
                  "Reservation not found for payment callback: id=" + reservationId);
            });
  }
}
