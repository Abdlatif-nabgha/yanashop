package com.nabgha.catalogue.domain.port.in;

import com.nabgha.catalogue.domain.model.ProductId;

public interface DeleteProductUseCase {

    void delete(ProductId id);
}
