package com.andrey999r.staynova.rooms.bl;

import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import java.util.List;

public interface RoomSearchService {

  void syncAll();

  void index(RoomEntity room);

  void delete(Long roomId);

  List<RoomDto> search(String query);
}
