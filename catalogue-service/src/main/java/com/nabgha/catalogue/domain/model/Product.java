package com.nabgha.catalogue.domain.model;


import com.nabgha.catalogue.domain.exception.InsufficientStockException;

import java.util.Objects;

public class Product {

    private final ProductId id;
    private String name;
    private String description;
    private Money price;
    private int stockQuantity;
    private Category category;

    /// Constructeur privé
    private Product(
            ProductId id,
            String name,
            String description ,
            Money price,
            int stockQuantity,
            Category category
    ) {
        this.id = Objects.requireNonNull(id, "ProductId cannot be null");
        this.name = Objects.requireNonNull(name, "name");
        this.description = description;
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative: " + stockQuantity);
        }
        this.stockQuantity = stockQuantity;
        this.category = Objects.requireNonNull(category, "category cannot be null");
        this.price = Objects.requireNonNull(price, "Product price cannot be null");
        if (price.isZero()) {
            throw new IllegalArgumentException("Product price cannot be zero");
        }
    }

    /**
     * Factory method to create a brand-new Product.
     * Generates a new ProductId.
     */
    public static Product create(
            String name,
            String description,
            Money price,
            int initialQuantity,
            Category category
    )
    {
        return new Product(
                ProductId.generate(),
                name,
                description,
                price,
                initialQuantity,
                category
        );
    }

    /**
     * Factory method to reconstruct a Product from persistence.
     * Use this when loading from DB.
     */
    public static Product restore(
            ProductId id,
            String name,
            String description,
            Money price,
            int stockQuantity,
            Category category
    )
    {
        return new Product(id, name, description, price, stockQuantity, category);
    }

    public void rename(String newName) {
        this.name = requireNonBlank(newName);
    }

    private String requireNonBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("name" + " cannot be blank");
        }
        return value;
    }

    public void changePriceTo(Money newPrice) {
        Objects.requireNonNull(newPrice, "New price cannot be null");
        if (newPrice.isZero()) {
            throw new IllegalArgumentException("price cannot be zero");
        }
        this.price = newPrice;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void recategorize(Category newCategory) {
        this.category = Objects.requireNonNull(newCategory, "category cannot be null");
    }

    /**
     * Reduces stock by the given quantity (e.g. after a sale).
     *
     * @throws InsufficientStockException if requested quantity exceeds current stock
     */

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
        if (quantity > stockQuantity) {
            throw new InsufficientStockException(id, quantity, stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Increases stock by the given quantity (e.g. after a restocking).
     */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
        this.stockQuantity += quantity;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }


    // ===== Accessors (no setters!) =====
    public ProductId id()         { return id; }
    public String name()          { return name; }
    public String description()   { return description; }
    public Money price()          { return price; }
    public int stockQuantity()    { return stockQuantity; }
    public Category category()    { return category; }

    // ===== Identity-based equality (Entity, not Value Object) =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;
        return id.equals(other.id);  // ← equality by identity
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
