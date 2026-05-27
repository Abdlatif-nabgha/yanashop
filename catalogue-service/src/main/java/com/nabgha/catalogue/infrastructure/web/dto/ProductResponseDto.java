package com.nabgha.catalogue.infrastructure.web.dto;


import com.nabgha.catalogue.domain.model.Category;
import com.nabgha.catalogue.domain.model.ProductId;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        int stockQuantity,
        Category category)
{

}
