package com.andrey999r.staynova.rooms.api.dto;

import com.andrey999r.staynova.availability.dal.AvailabilityStatus;
import java.math.BigDecimal;

public record RoomFilterDto(
    Long roomId,
    AvailabilityStatus availabilityStatus,
    BigDecimal price,
    String title,
    String description,
    Integer pageSize,
    Integer pageNumber,
    String sortByPrice) {}
