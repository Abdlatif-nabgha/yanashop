package com.nabgha.catalogue.domain.port.in;

import com.nabgha.catalogue.domain.model.Category;
import com.nabgha.catalogue.domain.model.ProductId;

import java.math.BigDecimal;

public record UpdateProductCommand(
        ProductId id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        Category category
) {
}
