package com.nabgha.catalogue.domain.exception;

import com.nabgha.catalogue.domain.model.ProductId;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(ProductId id) {
        super("Product not found with id: " + id);
    }
}
