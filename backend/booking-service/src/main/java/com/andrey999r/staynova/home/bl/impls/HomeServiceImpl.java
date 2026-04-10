package com.andrey999r.staynova.home.bl.impls;

import com.andrey999r.staynova.home.bl.HomeService;
import com.andrey999r.staynova.home.dto.BannerDto;
import com.andrey999r.staynova.home.dto.HomeDto;
import com.andrey999r.staynova.rooms.api.dto.PopularFilterDto;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.bl.impls.RoomServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeServiceImpl implements HomeService {
  private static final BannerDto BANNER =
      new BannerDto(
          "Добро пожаловать в Hotel 5★",
          "Лучшие номера для вашего отдыха и бизнеса",
          "http://localhost:9000/rooms/default-photo.png");
  private final RoomServiceImpl roomService;

  @Override
  public HomeDto getHome() {
    log.info("Building home page data");
    List<RoomDto> popularRooms = roomService.getAllPopularByFilter(new PopularFilterDto(6, 0));
    log.info("Home page built with {} popular rooms", popularRooms.size());
    return new HomeDto(BANNER, popularRooms);
  }
}
