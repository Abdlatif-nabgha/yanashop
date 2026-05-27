package com.nabgha.catalogue.infrastructure.web.dto;


import com.nabgha.catalogue.domain.model.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank(message = "Name is required")
        String name,
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letters ISO code")
        String currency,

        @Min(value = 0, message = "Initial quantity cannot be negative")
        int initialQuantity,

        @NotNull(message = "Category is required")
        Category category
)
{

}
