package com.andrey999r.staynova.hotel.bl.dto;

import com.andrey999r.staynova.hotel.Country;
import java.time.LocalDate;

public record HotelFilterDto(
    String query,
    Country country,
    LocalDate checkIn,
    LocalDate checkOut,
    Integer guests,
    Integer pageNumber,
    Integer pageSize) {}
