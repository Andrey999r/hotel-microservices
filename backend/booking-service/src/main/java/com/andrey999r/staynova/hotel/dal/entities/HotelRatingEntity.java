package com.andrey999r.staynova.hotel.dal.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(
    name = "hotel_rating",
    uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "user_login"}))
@Entity
@Getter
@Setter
@NoArgsConstructor
public class HotelRatingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false)
  private HotelEntity hotel;

  @Column(name = "user_login", nullable = false)
  private String userLogin;

  @Min(1)
  @Max(5)
  @Column(name = "rating", nullable = false)
  private Integer rating;

  @Column(name = "comment", columnDefinition = "TEXT")
  private String comment;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
