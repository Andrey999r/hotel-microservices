package com.andrey999r.staynova.hotel.bl;

import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelFilterDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelRatingDto;
import com.andrey999r.staynova.hotel.bl.dto.HotelReviewDto;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import java.util.List;

public interface HotelService {

  List<HotelDto> getPopularHotels(int page, int size);

  HotelDto getHotelById(Long hotelId);

  List<HotelDto> searchHotels(String query);

  List<HotelDto> filterHotels(HotelFilterDto filter);

  void addRating(Long hotelId, HotelRatingDto dto, String userLogin);

  List<HotelReviewDto> getReviews(Long hotelId);

  boolean canReview(Long hotelId, String userLogin);

  List<RoomDto> getRoomsForHotel(Long hotelId, String query);
}
