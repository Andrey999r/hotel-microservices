package com.domain.entities;

import com.domain.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "payment")
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payer_id", nullable = false)
  private PayerEntity payer;

  @Column(name = "reservation_id", nullable = false)
  private Long reservationId;

  @Column(name = "hotel_id", nullable = false)
  private Long hotelId;

  @Column(name = "room_id", nullable = false)
  private Long roomId;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus paymentStatus;

  @Column(name = "provider_session_id", unique = true)
  private String providerSessionId;

  @Column(name = "payment_url")
  private String paymentUrl;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "paid_at")
  private Instant paidAt;

  @Column(name = "cancelled_at")
  private Instant cancelledAt;
}
