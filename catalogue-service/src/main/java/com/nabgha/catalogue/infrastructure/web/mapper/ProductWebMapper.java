package com.nabgha.catalogue.infrastructure.web.mapper;


import com.nabgha.catalogue.domain.model.Product;
import com.nabgha.catalogue.domain.model.ProductId;
import com.nabgha.catalogue.domain.port.in.CreateProductCommand;
import com.nabgha.catalogue.domain.port.in.PatchProductCommand;
import com.nabgha.catalogue.domain.port.in.UpdateProductCommand;
import com.nabgha.catalogue.infrastructure.web.dto.PatchProductRequestDto;
import com.nabgha.catalogue.infrastructure.web.dto.ProductRequestDto;
import com.nabgha.catalogue.infrastructure.web.dto.ProductResponseDto;
import com.nabgha.catalogue.infrastructure.web.dto.UpdateProductRequestDto;

public class ProductWebMapper {

    private ProductWebMapper() {}

    public static CreateProductCommand toCommand(ProductRequestDto dto) {
        return new CreateProductCommand(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.initialQuantity(),
                dto.category()
        );
    }

    public static UpdateProductCommand toUpdateCommand(ProductId id, UpdateProductRequestDto dto) {
        return new UpdateProductCommand(
                id,
                dto.name(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.category()
        );
    }

    public static PatchProductCommand toPatchCommand(ProductId id, PatchProductRequestDto dto) {
        return new PatchProductCommand(
                id,
                dto.name(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.category(),
                dto.stockQuantity()
        );
    }

    public static ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(
                product.id().value(),
                product.name(),
                product.description(),
                product.price().amount(),
                product.price().currency().getCurrencyCode(),
                product.stockQuantity(),
                product.category()
        );
    }
}
