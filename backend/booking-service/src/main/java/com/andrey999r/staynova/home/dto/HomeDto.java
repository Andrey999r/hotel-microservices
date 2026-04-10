package com.andrey999r.staynova.home.dto;

import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import java.util.List;

public record HomeDto(BannerDto banner, List<RoomDto> popularRooms) {}
