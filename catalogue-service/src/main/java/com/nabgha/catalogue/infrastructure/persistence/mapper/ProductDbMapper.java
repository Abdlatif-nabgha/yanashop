package com.nabgha.catalogue.infrastructure.persistence.mapper;


import com.nabgha.catalogue.domain.model.Money;
import com.nabgha.catalogue.domain.model.Product;
import com.nabgha.catalogue.domain.model.ProductId;
import com.nabgha.catalogue.infrastructure.persistence.entity.ProductJpaEntity;

public class ProductDbMapper {

    private ProductDbMapper() {}

    public static ProductJpaEntity toEntity(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();

        entity.setId(product.id().value());
        entity.setName(product.name());
        entity.setDescription(product.description());
        entity.setPriceAmount(product.price().amount());
        entity.setPriceCurrency(product.price().currency().getCurrencyCode());
        entity.setStockQuantity(product.stockQuantity());
        entity.setCategory(product.category());

        return entity;
    }

    public static Product toDomain(ProductJpaEntity entity) {
        return Product.restore(
                new ProductId(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                Money.of(entity.getPriceAmount(), entity.getPriceCurrency()),
                entity.getStockQuantity(),
                entity.getCategory()
        );
    }
}
