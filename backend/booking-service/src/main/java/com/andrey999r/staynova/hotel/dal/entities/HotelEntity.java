package com.andrey999r.staynova.hotel.dal.entities;

import com.andrey999r.staynova.hotel.Country;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "hotel")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class HotelEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "address")
  private String address;

  @Column(name = "main_photo", nullable = false)
  private String mainPhotoUrl;

  @ElementCollection
  @CollectionTable(name = "hotel_photos", joinColumns = @JoinColumn(name = "hotel_id"))
  @Column(name = "photo_url")
  private List<String> galleryUrl = new ArrayList<>();

  @Column(name = "country")
  @Enumerated(EnumType.STRING)
  private Country country;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RoomEntity> rooms = new ArrayList<>();

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<HotelRatingEntity> ratings = new ArrayList<>();
}
