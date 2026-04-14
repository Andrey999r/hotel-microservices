package com.andrey999r.staynova.bl.impls;

import com.andrey999r.staynova.api.dto.WebhookDto;
import com.andrey999r.staynova.bl.PaymentService;
import com.andrey999r.staynova.dal.PayerRepository;
import com.andrey999r.staynova.dal.PaymentRepository;
import com.andrey999r.staynova.dal.PaymentStatus;
import com.andrey999r.staynova.dal.entities.PayerEntity;
import com.andrey999r.staynova.dal.entities.PaymentEntity;
import com.andrey999r.staynova.general.exceptions.custom.PayerNotFoundException;
import com.andrey999r.staynova.general.exceptions.custom.PaymentAlreadyProcessedException;
import com.andrey999r.staynova.general.exceptions.custom.PaymentNotFoundException;
import com.andrey999r.staynova.general.exceptions.custom.ProviderException;
import com.andrey999r.staynova.general.kafka.dto.request.CancelDto;
import com.andrey999r.staynova.general.kafka.dto.request.PayDto;
import com.andrey999r.staynova.bl.PaymentResultPublisher;
import com.andrey999r.staynova.api.dto.PaymentInitRequest;
import com.andrey999r.staynova.api.dto.PaymentInitResponse;
import com.andrey999r.staynova.bl.PaymentProvider;
import jakarta.transaction.Transactional;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

  private final PayerRepository payerRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentProvider paymentProvider;
  private final PaymentResultPublisher paymentResultPublisher;

  @Override
  @Transactional
  public void initiatePayment(PayDto payDto) {
    log.info(
        "Initiating payment: reservationId={}, user={}, roomId={}",
        payDto.reservationId(),
        payDto.userLogin(),
        payDto.roomId());

    PayerEntity payer = findOrCreatePayer(payDto.userLogin());

    PaymentInitRequest initRequest =
        new PaymentInitRequest(
            payDto.reservationId(),
            payDto.userLogin(),
            payDto.hotelId(),
            payDto.roomId(),
            payDto.startDate(),
            payDto.endDate());

    PaymentInitResponse initResponse = callProvider(initRequest);

    PaymentEntity payment =
        PaymentEntity.builder()
            .payer(payer)
            .reservationId(payDto.reservationId())
            .hotelId(payDto.hotelId())
            .roomId(payDto.roomId())
            .startDate(payDto.startDate())
            .endDate(payDto.endDate())
            .paymentStatus(PaymentStatus.PENDING)
            .providerSessionId(initResponse.sessionId())
            .paymentUrl(initResponse.paymentUrl())
            .createdAt(Instant.now())
            .build();
    paymentRepository.save(payment);

    log.info(
        "Payment created: id={}, sessionId={}, reservationId={}",
        payment.getId(),
        initResponse.sessionId(),
        payDto.reservationId());

    paymentResultPublisher.sendPaymentUrl(payDto.reservationId(), initResponse.paymentUrl());

    if (paymentProvider.isAutoConfirm()) {
      log.info(
          "Auto-confirming payment for reservationId={} (stub provider)",
          payDto.reservationId());
      payment.setPaymentStatus(PaymentStatus.APPROVED);
      payment.setPaidAt(Instant.now());
      paymentRepository.save(payment);
      paymentResultPublisher.sendPaymentResult(payDto.reservationId(), true, null);
    }
  }

  @Override
  @Transactional
  public void processWebhook(WebhookDto webhookDto) {
    log.info(
        "Processing webhook: sessionId={}, success={}",
        webhookDto.sessionId(),
        webhookDto.success());

    PaymentEntity payment =
        paymentRepository
            .findByProviderSessionId(webhookDto.sessionId())
            .orElseThrow(
                () -> {
                  log.warn("Payment not found for sessionId={}", webhookDto.sessionId());
                  return new PaymentNotFoundException(webhookDto.sessionId());
                });

    if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
      log.warn(
          "Payment id={} is already processed: status={}",
          payment.getId(),
          payment.getPaymentStatus());
      throw new PaymentAlreadyProcessedException(payment.getId(), payment.getPaymentStatus());
    }

    if (webhookDto.success()) {
      payment.setPaymentStatus(PaymentStatus.APPROVED);
      payment.setPaidAt(Instant.now());
      log.info(
          "Payment id={} approved for reservationId={}",
          payment.getId(),
          payment.getReservationId());
    } else {
      payment.setPaymentStatus(PaymentStatus.FAILED);
      payment.setCancelledAt(Instant.now());
      log.warn(
          "Payment id={} failed for reservationId={}, reason={}",
          payment.getId(),
          payment.getReservationId(),
          webhookDto.errorMessage());
    }
    paymentRepository.save(payment);

    paymentResultPublisher.sendPaymentResult(
        payment.getReservationId(), webhookDto.success(), webhookDto.errorMessage());
  }

  @Override
  @Transactional
  public void cancelPayment(CancelDto cancelDto) {
    log.info(
        "Cancelling payment: reservationId={}, user={}",
        cancelDto.reservationId(),
        cancelDto.userLogin());

    PayerEntity payer =
        payerRepository
            .findByLogin(cancelDto.userLogin())
            .orElseThrow(
                () -> {
                  log.warn("Payer not found for login={}", cancelDto.userLogin());
                  return new PayerNotFoundException(cancelDto.userLogin());
                });

    payer.getPayments().stream()
        .filter(
            p ->
                p.getReservationId() == cancelDto.reservationId()
                    && p.getPaymentStatus() == PaymentStatus.APPROVED)
        .forEach(
            p -> {
              p.setPaymentStatus(PaymentStatus.CANCELLED);
              p.setCancelledAt(Instant.now());
              paymentRepository.save(p);
              log.info(
                  "Payment id={} cancelled for reservationId={}",
                  p.getId(),
                  cancelDto.reservationId());
            });
  }

  private PayerEntity findOrCreatePayer(String login) {
    return payerRepository
        .findByLogin(login)
        .orElseGet(
            () -> {
              log.info("Creating new payer for login={}", login);
              return payerRepository.save(PayerEntity.builder().login(login).build());
            });
  }

  private PaymentInitResponse callProvider(PaymentInitRequest request) {
    try {
      return paymentProvider.initiate(request);
    } catch (Exception e) {
      log.error(
          "Payment provider failed for reservationId={}: {}",
          request.reservationId(),
          e.getMessage(),
          e);
      throw new ProviderException(e.getMessage(), e);
    }
  }
}
