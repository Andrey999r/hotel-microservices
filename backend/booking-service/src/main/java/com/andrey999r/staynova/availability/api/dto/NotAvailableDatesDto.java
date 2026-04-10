package com.andrey999r.staynova.availability.api.dto;

import java.time.LocalDate;

public record NotAvailableDatesDto(LocalDate startDate, LocalDate endDate) {}
