package com.andrey999r.staynova.rooms.bl;

import java.util.List;

public interface PopularityService {

  void init(Long roomId);

  void increment(Long roomId);

  void delete(Long roomId);

  List<Long> getTopRoomIds(int page, int size);
}
