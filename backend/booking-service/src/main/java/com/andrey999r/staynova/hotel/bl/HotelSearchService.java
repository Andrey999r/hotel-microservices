package com.andrey999r.staynova.hotel.bl;

import com.andrey999r.staynova.hotel.bl.dto.HotelDto;
import com.andrey999r.staynova.hotel.dal.entities.HotelEntity;
import java.util.List;

public interface HotelSearchService {

  void index(HotelEntity hotel);

  void delete(Long hotelId);

  void syncAll();

  List<HotelDto> search(String query);
}
