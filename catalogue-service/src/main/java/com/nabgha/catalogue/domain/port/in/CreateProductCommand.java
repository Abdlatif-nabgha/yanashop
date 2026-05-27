package com.nabgha.catalogue.domain.port.in;


import com.nabgha.catalogue.domain.model.Category;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal price,
        String currency,
        int initialQuantity,
        Category category
)
{
}
