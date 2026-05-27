package com.nabgha.catalogue.domain.exception;


import com.nabgha.catalogue.domain.model.ProductId;

public class InsufficientStockException extends RuntimeException {
    private final ProductId productId;
    private final int requested;
    private final int available;

    public InsufficientStockException(ProductId productId, int requested, int available) {
        super(String.format("Insufficient stock for product %s: requested %d,  available %d", productId, requested, available));
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    public ProductId productId() { return productId; }
    public int requested()       { return requested; }
    public int available()       { return available; }

}
