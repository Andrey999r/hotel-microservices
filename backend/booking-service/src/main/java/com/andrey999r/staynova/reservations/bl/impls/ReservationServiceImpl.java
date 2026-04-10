package com.andrey999r.staynova.reservations.bl.impls;

import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;
import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.general.RoleProperties;
import com.andrey999r.staynova.general.exception.custom.reservation.CancellationDeadlineException;
import com.andrey999r.staynova.general.exception.custom.reservation.InvalidDateRangeException;
import com.andrey999r.staynova.general.exception.custom.reservation.InvalidReservationStatusException;
import com.andrey999r.staynova.general.exception.custom.reservation.ReservationAccessDeniedException;
import com.andrey999r.staynova.general.exception.custom.reservation.ReservationAlreadyCancelledException;
import com.andrey999r.staynova.general.exception.custom.reservation.ReservationConflictException;
import com.andrey999r.staynova.general.exception.custom.reservation.ReservationNotFoundException;
import com.andrey999r.staynova.general.kafka.notification.NotificationPublisher;
import com.andrey999r.staynova.general.kafka.payment.PaymentEventPublisher;
import com.andrey999r.staynova.general.kafka.payment.dto.CancelPaymentKafkaDto;
import com.andrey999r.staynova.general.kafka.payment.dto.PaymentRequestKafkaDto;
import com.andrey999r.staynova.general.security.SecurityUtils;
import com.andrey999r.staynova.reservations.api.dto.ReservationDto;
import com.andrey999r.staynova.reservations.api.dto.ReservationFilterDto;
import com.andrey999r.staynova.reservations.bl.ReservationService;
import com.andrey999r.staynova.reservations.dal.ReservationRepository;
import com.andrey999r.staynova.reservations.dal.ReservationStatus;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import com.andrey999r.staynova.rooms.bl.RoomService;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

  private final RoleProperties roleProperties;
  private final ReservationRepository reservationRepository;
  private final Mapper mapper;
  private final RoomService roomService;
  private final NotificationPublisher notificationProducer;
  private final PaymentEventPublisher paymentRequestProducer;
  private final SecurityUtils securityUtils;

  @Override
  public ReservationEntity getReservationById(Long id) {
    log.info("Getting reservation by id={}", id);
    List<String> roles = securityUtils.getRoles();
    String userLogin = securityUtils.getUserLogin();
    ReservationEntity found =
        reservationRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("Reservation not found: id={}", id);
                  return new ReservationNotFoundException(id);
                });
    if (!roles.contains(roleProperties.getAdminRole()) && !found.getUserLogin().equals(userLogin)) {
      log.warn(
          "Access denied: user={} tried to access reservation id={} owned by {}",
          userLogin,
          id,
          found.getUserLogin());
      throw new ReservationAccessDeniedException();
    }
    log.info("Returning reservation id={} for user={}", id, userLogin);
    return found;
  }

  @Override
  public List<ReservationEntity> getReservations(ReservationFilterDto filter) {
    String userLogin = securityUtils.getUserLogin();
    List<String> roles = securityUtils.getRoles();
    log.info(
        "Getting reservations for user={}, isAdmin={}",
        userLogin,
        roles.contains(roleProperties.getAdminRole()));

    Integer pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
    Integer pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;
    Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

    String loginForFilter = roles.contains(roleProperties.getAdminRole()) ? null : userLogin;

    List<ReservationEntity> entities =
        reservationRepository.findAllbyFilter(filter.roomId(), loginForFilter, pageable);

    log.info("Returning {} reservations for user={}", entities.size(), userLogin);
    return entities;
  }

  @Override
  @Transactional
  public ReservationEntity bookReservation(ReservationDto reservationToBook) {
    String userLogin = securityUtils.getUserLogin();
    log.info(
        "Booking reservation: user={}, roomId={}, from={} to={}",
        userLogin,
        reservationToBook.roomId(),
        reservationToBook.startDate(),
        reservationToBook.endDate());

    if (reservationToBook.reservationStatus() != null) {
      log.warn("Reservation status provided on booking by user={}", userLogin);
      throw new InvalidReservationStatusException();
    }
    if (!reservationToBook.startDate().isBefore(reservationToBook.endDate())) {
      log.warn(
          "Invalid date range: start={}, end={}",
          reservationToBook.startDate(),
          reservationToBook.endDate());
      throw new InvalidDateRangeException();
    }

    List<ReservationEntity> conflicts =
        reservationRepository.findActiveConflicts(
            reservationToBook.startDate(),
            reservationToBook.endDate(),
            reservationToBook.roomId(),
            null);
    if (!conflicts.isEmpty()) {
      log.warn(
          "Booking conflict: roomId={}, from={} to={} overlaps with {} active reservation(s)",
          reservationToBook.roomId(),
          reservationToBook.startDate(),
          reservationToBook.endDate(),
          conflicts.size());
      throw new ReservationConflictException();
    }

    RoomEntity roomEntity = roomService.getRoomEntityById(reservationToBook.roomId());
    ReservationEntity newReservation = mapper.reservationToEntity(reservationToBook, roomEntity);
    newReservation.setReservationStatus(ReservationStatus.PENDING);
    newReservation.setUserLogin(userLogin);
    ReservationEntity saved = reservationRepository.save(newReservation);

    notificationProducer.send(
        userLogin,
        "Ваша бронь «"
            + roomEntity.getTitle()
            + "» с "
            + reservationToBook.startDate()
            + " по "
            + reservationToBook.endDate()
            + " создана. Ожидайте подтверждения оплаты.",
        NotificationEventType.BOOKING_CREATED);

    PaymentRequestKafkaDto paymentRequest =
        new PaymentRequestKafkaDto(
            saved.getId(),
            userLogin,
            roomEntity.getHotel().getId(),
            roomEntity.getId(),
            reservationToBook.startDate(),
            reservationToBook.endDate());
    paymentRequestProducer.sendPaymentRequest(paymentRequest);

    log.info(
        "Reservation saved as PENDING, payment request sent: id={}, user={}, roomId={}",
        saved.getId(),
        userLogin,
        saved.getRoom().getId());
    return saved;
  }

  @Override
  @Transactional
  public void cancelReservation(Long id) {
    String userLogin = securityUtils.getUserLogin();
    List<String> roles = securityUtils.getRoles();
    log.info("Cancelling reservation id={} by user={}", id, userLogin);
    ReservationEntity toDelete =
        reservationRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  log.warn("Reservation not found for cancellation: id={}", id);
                  return new ReservationNotFoundException(id);
                });

    if (!roles.contains(roleProperties.getAdminRole())
        && !toDelete.getUserLogin().equals(userLogin)) {
      log.warn(
          "Access denied: user={} tried to cancel reservation id={} owned by {}",
          userLogin,
          id,
          toDelete.getUserLogin());
      throw new ReservationAccessDeniedException();
    }
    if (toDelete.getReservationStatus().equals(ReservationStatus.CANCELLED)) {
      log.warn("Reservation id={} is already cancelled", id);
      throw new ReservationAlreadyCancelledException(id);
    }

    boolean isAdmin = roles.contains(roleProperties.getAdminRole());
    if (!isAdmin) {
      LocalDate deadline = toDelete.getStartDate().minusDays(2);
      if (LocalDate.now().isAfter(deadline)) {
        log.warn(
            "Cancellation deadline passed for reservation id={}: startDate={}, deadline={}",
            id,
            toDelete.getStartDate(),
            deadline);
        throw new CancellationDeadlineException();
      }
    }

    notificationProducer.send(
        toDelete.getUserLogin(),
        "Ваша бронь «"
            + toDelete.getRoom().getTitle()
            + "» с "
            + toDelete.getStartDate()
            + " по "
            + toDelete.getEndDate()
            + " отменена.",
        NotificationEventType.BOOKING_CANCELLED);
    reservationRepository.setStatus(id, ReservationStatus.CANCELLED);

    CancelPaymentKafkaDto cancelRequest =
        new CancelPaymentKafkaDto(
            id,
            toDelete.getUserLogin(),
            toDelete.getRoom().getId(),
            toDelete.getRoom().getHotel().getId());
    paymentRequestProducer.sendCancelRequest(cancelRequest);

    log.info("Reservation id={} cancelled and payment cancellation sent", id);
  }
}
