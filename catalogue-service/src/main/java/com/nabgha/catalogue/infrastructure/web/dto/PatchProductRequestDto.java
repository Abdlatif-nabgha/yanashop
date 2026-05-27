package com.nabgha.catalogue.infrastructure.web.dto;

import com.nabgha.catalogue.domain.model.Category;

import java.math.BigDecimal;

public record PatchProductRequestDto(
        String name,
        String description,
        BigDecimal price,
        String currency,
        Category category,
        Integer stockQuantity
) {
}
