package com.andrey999r.staynova.rooms.api.dto;

import com.andrey999r.staynova.availability.dal.AvailabilityStatus;
import com.andrey999r.staynova.rooms.dal.Currency;
import com.andrey999r.staynova.rooms.dal.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.List;

public record RoomDto(
    @Null Long id,
    @NotNull RoomType roomType,
    @NotNull AvailabilityStatus availabilityStatus,
    @Null String mainPhotoUrl,
    @Null List<String> photoUrls,
    String description,
    String title,
    BigDecimal price,
    Currency currency,
    Integer maxGuests,
    Long hotelId) {}
