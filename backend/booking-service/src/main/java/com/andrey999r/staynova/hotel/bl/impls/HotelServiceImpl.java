package com.andrey999r.staynova.hotel.bl.impls;

import com.andrey999r.staynova.general.Mapper;
import com.andrey999r.staynova.general.exception.custom.staynova.HotelNotFoundException;
import com.andrey999r.staynova.general.exception.custom.staynova.InvalidHotelIdException;
import com.andrey999r.staynova.general.exception.custom.staynova.InvalidSearchQueryException;
import com.andrey999r.staynova.general.exception.custom.staynova.NoCompletedStayException;
import com.andrey999r.staynova.general.exception.custom.staynova.RatingAlreadyExistsException;
import com.andrey999r.staynova.hotel.bl.HotelPopularityService;
import com.andrey999r.staynova.hotel.bl.HotelSearchService;
import com.andrey999r.staynova.hotel.bl.HotelService;
import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelFilterDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelRatingDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelReviewDto;
import com.andrey999r.staynova.hotel.dal.HotelRatingRepository;
import com.andrey999r.staynova.hotel.dal.HotelRepository;
import com.andrey999r.staynova.hotel.dal.entities.HotelEntity;
import com.andrey999r.staynova.hotel.dal.entities.HotelRatingEntity;
import com.andrey999r.staynova.reservations.dal.ReservationRepository;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.dal.RoomRepository;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

  private final HotelRepository hotelRepository;
  private final HotelRatingRepository hotelRatingRepository;
  private final HotelSearchService hotelSearchService;
  private final HotelPopularityService hotelPopularityService;
  private final RoomRepository roomRepository;
  private final ReservationRepository reservationRepository;
  private final Mapper mapper;

  @Override
  @Transactional
  public List<HotelDto> getPopularHotels(int page, int size) {
    log.info("Getting popular hotels page={}, size={}", page, size);
    List<Long> topIds = hotelPopularityService.getTopHotelIds(page, size);
    if (topIds.isEmpty()) {
      log.info("Redis popularity empty, falling back to DB for popular hotels");
      Pageable pageable = PageRequest.of(page, size);
      List<HotelEntity> hotels = hotelRepository.findAll(pageable).getContent();
      List<HotelDto> fallback =
          hotels.stream()
              .map(h -> toDto(h, false))
              .sorted(Comparator.comparingDouble(HotelDto::averageRating).reversed())
              .toList();
      log.info("Returning {} popular hotels (fallback)", fallback.size());
      return fallback;
    }
    List<HotelDto> result = new ArrayList<>();
    for (Long id : topIds) {
      hotelRepository.findById(id).ifPresent(h -> result.add(toDto(h, false)));
    }
    log.info("Returning {} popular hotels", result.size());
    return result;
  }

  @Override
  @Transactional
  public HotelDto getHotelById(Long hotelId) {
    if (hotelId == null || hotelId < 1) {
      log.warn("Invalid hotel id: {}", hotelId);
      throw new InvalidHotelIdException();
    }
    log.info("Getting hotel by id={}", hotelId);
    HotelEntity hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(
                () -> {
                  log.warn("Hotel not found: id={}", hotelId);
                  return new HotelNotFoundException(hotelId);
                });
    hotelPopularityService.increment(hotelId);
    return toDto(hotel, true);
  }

  @Override
  public List<HotelDto> searchHotels(String query) {
    if (query == null || query.isBlank() || query.length() > 200) {
      log.warn("Invalid hotel search query: '{}'", query);
      throw new InvalidSearchQueryException();
    }
    log.info("Searching hotels by query='{}'", query);
    List<HotelDto> results = hotelSearchService.search(query);
    log.info("Hotel search returned {} results for query='{}'", results.size(), query);
    return results;
  }

  @Override
  @Transactional
  public List<HotelDto> filterHotels(HotelFilterDto filter) {
    log.info(
        "Filtering hotels: country={}, guests={}, checkIn={}, checkOut={}",
        filter.country(),
        filter.guests(),
        filter.checkIn(),
        filter.checkOut());

    int page = filter.pageNumber() != null ? filter.pageNumber() : 0;
    int size = filter.pageSize() != null ? filter.pageSize() : 12;

    List<HotelEntity> hotels;
    boolean hasDateFilter = filter.checkIn() != null && filter.checkOut() != null;
    boolean hasGuestFilter = filter.guests() != null && filter.guests() > 0;

    if (hasDateFilter || hasGuestFilter) {
      LocalDate checkIn = filter.checkIn() != null ? filter.checkIn() : LocalDate.now();
      LocalDate checkOut = filter.checkOut() != null ? filter.checkOut() : checkIn.plusDays(1);
      hotels =
          hotelRepository.findAvailableHotels(
              filter.country(), filter.guests(), checkIn, checkOut, Pageable.unpaged());
    } else {
      hotels = hotelRepository.findAllByCountry(filter.country(), Pageable.unpaged());
    }

    List<HotelDto> all =
        hotels.stream()
            .map(h -> toDto(h, false))
            .sorted(
                Comparator.comparingDouble(
                        (HotelDto dto) -> hotelPopularityService.getScore(dto.id()))
                    .reversed())
            .toList();

    int fromIndex = page * size;
    if (fromIndex >= all.size()) {
      log.info("Filter returned 0 hotels (page={} out of range)", page);
      return List.of();
    }
    int toIndex = Math.min(fromIndex + size, all.size());
    List<HotelDto> result = all.subList(fromIndex, toIndex);
    log.info("Filter returned {} hotels (page={}, size={})", result.size(), page, size);
    return result;
  }

  @Override
  @Transactional
  public void addRating(Long hotelId, HotelRatingDto dto, String userLogin) {
    if (hotelId == null || hotelId < 1) {
      throw new InvalidHotelIdException();
    }
    log.info("Adding rating {} for hotel id={} by user='{}'", dto.rating(), hotelId, userLogin);
    HotelEntity hotel =
        hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(hotelId));

    if (!reservationRepository.existsCompletedStay(userLogin, hotelId, LocalDate.now())) {
      log.warn("User '{}' has no completed stay at hotel id={}", userLogin, hotelId);
      throw new NoCompletedStayException();
    }

    if (hotelRatingRepository.existsByHotelIdAndUserLogin(hotelId, userLogin)) {
      log.warn("User '{}' already rated hotel id={}", userLogin, hotelId);
      throw new RatingAlreadyExistsException(userLogin, hotelId);
    }

    HotelRatingEntity rating = new HotelRatingEntity();
    rating.setHotel(hotel);
    rating.setUserLogin(userLogin);
    rating.setRating(dto.rating());
    rating.setComment(dto.comment());
    hotelRatingRepository.save(rating);
    log.info("Rating saved for hotel id={} by user='{}'", hotelId, userLogin);
  }

  @Override
  public boolean canReview(Long hotelId, String userLogin) {
    if (hotelId == null || hotelId < 1 || userLogin == null || userLogin.isBlank()) {
      return false;
    }
    if (!hotelRepository.existsById(hotelId)) {
      return false;
    }
    boolean hasCompletedStay =
        reservationRepository.existsCompletedStay(userLogin, hotelId, LocalDate.now());
    boolean alreadyReviewed = hotelRatingRepository.existsByHotelIdAndUserLogin(hotelId, userLogin);
    return hasCompletedStay && !alreadyReviewed;
  }

  @Override
  @Transactional
  public List<HotelReviewDto> getReviews(Long hotelId) {
    if (hotelId == null || hotelId < 1) {
      throw new InvalidHotelIdException();
    }
    if (!hotelRepository.existsById(hotelId)) {
      throw new HotelNotFoundException(hotelId);
    }
    log.info("Getting reviews for hotel id={}", hotelId);
    List<Object[]> rows = hotelRatingRepository.findReviewsByHotelId(hotelId);
    return rows.stream()
        .map(
            row ->
                new HotelReviewDto(
                    (String) row[0],
                    row[1] != null ? (String) row[1] : "—",
                    row[2] != null ? ((Number) row[2]).longValue() : 0L,
                    ((Number) row[3]).intValue(),
                    (String) row[4],
                    row[5] != null ? ((java.sql.Timestamp) row[5]).toLocalDateTime() : null))
        .toList();
  }

  @Override
  @Transactional
  public List<RoomDto> getRoomsForHotel(Long hotelId, String query) {
    if (hotelId == null || hotelId < 1) {
      throw new InvalidHotelIdException();
    }
    log.info("Getting rooms for hotel id={}, query='{}'", hotelId, query);
    if (!hotelRepository.existsById(hotelId)) {
      throw new HotelNotFoundException(hotelId);
    }
    List<RoomEntity> rooms;
    if (query != null && !query.isBlank()) {
      String likeQuery = "%" + query.toLowerCase() + "%";
      rooms = roomRepository.findByHotelIdAndQuery(hotelId, likeQuery);
    } else {
      rooms = roomRepository.findByHotelId(hotelId);
    }
    log.info("Returning {} rooms for hotel id={}", rooms.size(), hotelId);
    return rooms.stream().map(mapper::roomToDomain).toList();
  }

  private HotelDto toDto(HotelEntity h, boolean includeRooms) {
    Double avgRating = hotelRatingRepository.getAverageRating(h.getId());
    Long ratingCount = hotelRatingRepository.countByHotelId(h.getId());
    List<RoomDto> rooms =
        includeRooms ? h.getRooms().stream().map(mapper::roomToDomain).toList() : List.of();
    return new HotelDto(
        h.getId(),
        h.getName(),
        h.getDescription(),
        h.getAddress(),
        h.getMainPhotoUrl(),
        h.getGalleryUrl(),
        avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0,
        ratingCount != null ? ratingCount : 0L,
        h.getCountry(),
        rooms);
  }
}
