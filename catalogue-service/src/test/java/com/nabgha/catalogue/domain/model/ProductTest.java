package com.nabgha.catalogue.domain.model;

import com.nabgha.catalogue.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void should_decrease_stock_successfully() {
        // Arrange (Préparation)
        Product product = Product.create(
                "iPhone 15", "Un téléphone", Money.of(999.0, "EUR"), 10, Category.ELECTRONICS
        );

        // Act (Action)
        product.decreaseStock(3);

        // Assert (Vérification)
        assertThat(product.stockQuantity()).isEqualTo(7);
    }

    @Test
    void should_throw_exception_when_decreasing_more_than_available_stock() {
        Product product = Product.create(
                "T-Shirt", "Vêtement", Money.of(20.0, "EUR"), 5, Category.CLOTHING
        );

        assertThatThrownBy(() -> product.decreaseStock(6))
                .isInstanceOf(InsufficientStockException.class);
    }
}