package com.andrey999r.staynova.hotel.bl.dto;

import com.andrey999r.staynova.hotel.Country;

public record UpdateHotelDto(String name, String description, String address, Country country) {}
