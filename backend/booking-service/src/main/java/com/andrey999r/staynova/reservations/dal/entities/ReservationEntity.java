package com.andrey999r.staynova.reservations.dal.entities;

import com.andrey999r.staynova.reservations.dal.ReservationStatus;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "reservation")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_login", nullable = false)
  private String userLogin;

  @ManyToOne
  @JoinColumn(name = "room_id")
  private RoomEntity room;

  @Column(name = "start_date_of_reservation", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date_of_reservation", nullable = false)
  private LocalDate endDate;

  @Column(name = "status_of_reservation", nullable = false)
  @Enumerated(EnumType.STRING)
  private ReservationStatus reservationStatus;

  @Column(name = "payment_url")
  private String paymentUrl;
}
