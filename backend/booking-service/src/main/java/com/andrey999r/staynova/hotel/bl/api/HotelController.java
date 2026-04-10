package com.andrey999r.staynova.hotel.bl.api;

import com.andrey999r.staynova.hotel.Country;
import com.andrey999r.staynova.hotel.bl.HotelService;
import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelFilterDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelRatingDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelReviewDto;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

  public static final String POPULAR = "/popular";
  public static final String SEARCH = "/search";
  public static final String FILTER = "/filter";
  public static final String BY_ID = "/{id}";
  public static final String ROOMS = "/{id}/rooms";
  public static final String RATING = "/{id}/rating";
  public static final String REVIEWS = "/{id}/reviews";
  public static final String CAN_REVIEW = "/{id}/can-review";

  private final HotelService hotelService;

  @GetMapping(POPULAR)
  public ResponseEntity<List<HotelDto>> getPopular(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size) {
    log.info("GET /hotels/popular page={}, size={}", page, size);
    return ResponseEntity.ok(hotelService.getPopularHotels(page, size));
  }

  @GetMapping(SEARCH)
  public ResponseEntity<List<HotelDto>> search(@RequestParam String query) {
    log.info("GET /hotels/search query='{}'", query);
    return ResponseEntity.ok(hotelService.searchHotels(query));
  }

  @GetMapping(FILTER)
  public ResponseEntity<List<HotelDto>> filter(
      @RequestParam(required = false) Country country,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate checkIn,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate checkOut,
      @RequestParam(required = false) Integer guests,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "12") int pageSize) {
    log.info(
        "GET /hotels/filter country={}, checkIn={}, checkOut={}, guests={}",
        country,
        checkIn,
        checkOut,
        guests);
    HotelFilterDto filter =
        new HotelFilterDto(null, country, checkIn, checkOut, guests, pageNumber, pageSize);
    return ResponseEntity.ok(hotelService.filterHotels(filter));
  }

  @GetMapping(BY_ID)
  public ResponseEntity<HotelDto> getById(@PathVariable("id") Long hotelId) {
    log.info("GET /hotels/{}", hotelId);
    return ResponseEntity.ok(hotelService.getHotelById(hotelId));
  }

  @GetMapping(ROOMS)
  public ResponseEntity<List<RoomDto>> getRooms(
      @PathVariable("id") Long hotelId, @RequestParam(required = false) String query) {
    log.info("GET /hotels/{}/rooms query='{}'", hotelId, query);
    return ResponseEntity.ok(hotelService.getRoomsForHotel(hotelId, query));
  }

  @GetMapping(CAN_REVIEW)
  public ResponseEntity<Boolean> canReview(
      @PathVariable("id") Long hotelId,
      @RequestHeader(value = "X-User-Login", required = false) String userLogin) {
    log.info("GET /hotels/{}/can-review for user='{}'", hotelId, userLogin);
    return ResponseEntity.ok(hotelService.canReview(hotelId, userLogin));
  }

  @GetMapping(REVIEWS)
  public ResponseEntity<List<HotelReviewDto>> getReviews(@PathVariable("id") Long hotelId) {
    log.info("GET /hotels/{}/reviews", hotelId);
    return ResponseEntity.ok(hotelService.getReviews(hotelId));
  }

  @PostMapping(RATING)
  public ResponseEntity<Void> addRating(
      @PathVariable("id") Long hotelId,
      @RequestBody @Valid HotelRatingDto dto,
      @RequestHeader(value = "X-User-Login", required = false) String userLogin) {
    log.info("POST /hotels/{}/rating by user='{}'", hotelId, userLogin);
    hotelService.addRating(hotelId, dto, userLogin);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
