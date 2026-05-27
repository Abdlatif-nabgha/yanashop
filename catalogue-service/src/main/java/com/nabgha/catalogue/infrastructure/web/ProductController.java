package com.nabgha.catalogue.infrastructure.web;

import com.nabgha.catalogue.domain.model.Product;
import com.nabgha.catalogue.domain.model.ProductId;
import com.nabgha.catalogue.domain.port.in.*;
import com.nabgha.catalogue.infrastructure.web.dto.ApiResponse;
import com.nabgha.catalogue.infrastructure.web.dto.PatchProductRequestDto;
import com.nabgha.catalogue.infrastructure.web.dto.ProductRequestDto;
import com.nabgha.catalogue.infrastructure.web.dto.ProductResponseDto;
import com.nabgha.catalogue.infrastructure.web.dto.UpdateProductRequestDto;
import com.nabgha.catalogue.infrastructure.web.mapper.ProductWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final PatchProductUseCase patchProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    /// 1. Create Product
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(
            @RequestBody @Valid ProductRequestDto dto) {

        Product product = createProductUseCase.execute(ProductWebMapper.toCommand(dto));
        ProductResponseDto responseDto = ProductWebMapper.toResponse(product);
        URI location = URI.create("/api/v1/products/" + product.id().value());

        return ResponseEntity.created(location)
                .body(ApiResponse.success("Product created successfully", responseDto));
    }

    ///  2. Get Product
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getById(@PathVariable("id") UUID id) {
        return getProductUseCase.execute(ProductId.of(id))
                .map(ProductWebMapper::toResponse)
                .map(dto -> ResponseEntity.ok(ApiResponse.success("Product found", dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Product not found with id: " + id)));
    }

    ///  3. Get All Products
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAll() {
        List<ProductResponseDto> products = getAllProductsUseCase.execute().stream()
                .map(ProductWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    ///  4. Edit Product
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> update(
            @PathVariable("id") UUID id,
            @RequestBody @Valid UpdateProductRequestDto dto) {

        Product product = updateProductUseCase.execute(
                ProductWebMapper.toUpdateCommand(ProductId.of(id), dto));

        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", ProductWebMapper.toResponse(product)));
    }

    /// 5. Patch Product
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> patch(
            @PathVariable("id") UUID id,
            @RequestBody PatchProductRequestDto dto) {

        Product product = patchProductUseCase.execute(
                ProductWebMapper.toPatchCommand(ProductId.of(id), dto));

        return ResponseEntity.ok(
                ApiResponse.success("Product patched successfully", ProductWebMapper.toResponse(product)));
    }

    /// 6. Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") UUID id) {
        deleteProductUseCase.delete(ProductId.of(id));
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

}
