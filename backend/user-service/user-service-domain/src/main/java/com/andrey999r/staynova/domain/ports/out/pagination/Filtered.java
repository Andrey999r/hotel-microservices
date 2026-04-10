package com.andrey999r.staynova.domain.ports.out.pagination;

import java.util.List;

public record Filtered<T>(List<T> content, int page, int size) {}
