package com.andrey999r.staynova.rooms.dal.entities;

import com.andrey999r.staynova.availability.dal.AvailabilityStatus;
import com.andrey999r.staynova.hotel.dal.entities.HotelEntity;
import com.andrey999r.staynova.reservations.dal.entities.ReservationEntity;
import com.andrey999r.staynova.rooms.dal.Currency;
import com.andrey999r.staynova.rooms.dal.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "room")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RoomEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "type_of_the_room", nullable = false)
  @Enumerated(EnumType.STRING)
  private RoomType roomType;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private AvailabilityStatus availabilityStatus;

  @Column(name = "photo", nullable = false)
  private String mainPhotoUrl;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "title", nullable = false)
  private String title;

  @Min(1)
  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @ElementCollection
  @CollectionTable(name = "room_photos", joinColumns = @JoinColumn(name = "room_id"))
  @Column(name = "photo_url")
  private List<String> photoUrls = new ArrayList<>();

  @Column(name = "currency", nullable = false)
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Min(1)
  @Column(name = "max_guests", nullable = false)
  private Integer maxGuests = 1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id")
  private HotelEntity hotel;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReservationEntity> reservations = new ArrayList<>();

  public RoomEntity(
      RoomType roomType,
      AvailabilityStatus availabilityStatus,
      Long id,
      String mainPhotoUrl,
      String description,
      String title,
      BigDecimal price,
      List<String> photoUrls,
      Currency currency) {
    this.roomType = roomType;
    this.availabilityStatus = availabilityStatus;
    this.id = id;
    this.mainPhotoUrl = mainPhotoUrl;
    this.description = description;
    this.title = title;
    this.price = price;
    this.photoUrls = photoUrls;
    this.currency = currency;
    this.maxGuests = 1;
  }
}
