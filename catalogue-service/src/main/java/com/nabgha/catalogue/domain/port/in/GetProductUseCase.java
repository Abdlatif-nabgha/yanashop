package com.nabgha.catalogue.domain.port.in;

import com.nabgha.catalogue.domain.model.Product;
import com.nabgha.catalogue.domain.model.ProductId;

import java.util.Optional;

public interface GetProductUseCase {

    Optional<Product> execute(ProductId id);
}