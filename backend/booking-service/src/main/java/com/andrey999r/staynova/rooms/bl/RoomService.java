package com.andrey999r.staynova.rooms.bl;

import com.andrey999r.staynova.rooms.api.dto.PopularFilterDto;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.api.dto.RoomFilterDto;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import java.util.List;

public interface RoomService {

  RoomEntity getRoomEntityById(Long roomId);

  RoomDto getRoomById(Long roomId);

  String getPhotoById(long roomId, long photoId);

  List<RoomDto> getAllRoomsByFilter(RoomFilterDto filter);

  List<RoomDto> search(String query);

  List<RoomDto> getAllPopularByFilter(PopularFilterDto filter);
}
