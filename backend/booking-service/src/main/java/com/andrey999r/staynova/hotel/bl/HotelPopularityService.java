package com.andrey999r.staynova.hotel.bl;

import java.util.List;

public interface HotelPopularityService {

  void init(Long hotelId);

  void increment(Long hotelId);

  void delete(Long hotelId);

  List<Long> getTopHotelIds(int page, int size);

  double getScore(Long hotelId);
}
