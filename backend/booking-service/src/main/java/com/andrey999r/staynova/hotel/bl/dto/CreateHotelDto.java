package com.andrey999r.staynova.hotel.bl.dto;

import com.andrey999r.staynova.hotel.Country;
import jakarta.validation.constraints.NotBlank;

public record CreateHotelDto(
    @NotBlank String name, String description, String address, Country country) {}
