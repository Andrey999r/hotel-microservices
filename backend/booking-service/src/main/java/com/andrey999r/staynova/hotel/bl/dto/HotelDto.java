package com.andrey999r.staynova.hotel.bl.dto;

import com.andrey999r.staynova.hotel.Country;
import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import java.util.List;

public record HotelDto(
    Long id,
    String name,
    String description,
    String address,
    String mainPhotoUrl,
    List<String> galleryUrl,
    Double averageRating,
    Long ratingCount,
    Country country,
    List<RoomDto> rooms) {}
