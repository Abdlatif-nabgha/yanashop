package com.nabgha.catalogue.domain.model;


import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    // Compact constructor (validation)
    public ProductId(UUID value) {
        this.value = Objects.requireNonNull(value, "ProductId value cannot be null");
    }

    // Factory methods — unchangeable
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId fromString(String value) {
        Objects.requireNonNull(value, "ProductId string cannot be null");
        try {
            return new ProductId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid product format: " + value, e);
        }
    }
}
